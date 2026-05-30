# JMeter + Jenkins + Grafana

Job da AC de **Qualidade e Teste de Software** que executa um **teste de
carga** com **Apache JMeter** orquestrado por um pipeline declarativo do
**Jenkins**, enviando as métricas em **tempo real** para o **InfluxDB** e
visualizando-as em **dashboards do Grafana**.

A integração segue a abordagem do tutorial de referência da BlazeMeter
([JMeter + Grafana](https://www.blazemeter.com/blog/jmeter-grafana)), porém
modernizada: em vez do protocolo Graphite, usamos o **Backend Listener**
nativo do JMeter (`InfluxdbBackendListenerClient`), que escreve direto na
API HTTP do InfluxDB.

---

## Demonstração em vídeo

Vídeo da aplicação rodando o job corretamente (pipeline do Jenkins
executando o JMeter e métricas chegando ao Grafana em tempo real):

[`Jmeter_Jenkins_Grafana.mp4`](Jmeter_Jenkins_Grafana.mp4)

---

## 1. Arquitetura

```
                         (1) sh jmeter -n -t test-plan.jmx ...
 ┌──────────────┐  pipeline   ┌────────────────────────────┐
 │   Jenkins    │────────────▶│  JMeter (dentro do ac-jenkins) │
 │  ac-jenkins  │             └──────────────┬─────────────┘
 └──────────────┘                            │ (2) Backend Listener
        ▲                                    │     escreve métricas
        │ HTML report / perfReport           ▼
        │                            ┌────────────────┐
        │                            │   InfluxDB 1.8  │  (banco "jmeter")
        │                            │   ac-influxdb   │
        │                            └────────┬───────┘
        │                                     │ (3) datasource
        │                                     ▼
        │                            ┌────────────────┐
        └─────────  navegador  ─────▶│    Grafana     │  http://localhost:3000
                                     │   ac-grafana   │  dashboards em tempo real
                                     └────────────────┘
```

1. O Jenkins roda o JMeter em modo **não-GUI** (`-n`) dentro do contêiner
   `ac-jenkins`.
2. O **Backend Listener** do plano de teste envia as métricas (tempo de
   resposta, throughput, erros, usuários ativos) para o **InfluxDB**.
3. O **Grafana** lê o InfluxDB como *datasource* e desenha os
   **dashboards** atualizando a cada 5 s.

---

## 2. Estrutura de arquivos

```
job/JMeter_Jenkins_Grafana/
├── Jenkinsfile                                  # Pipeline declarativo
├── test-plan.jmx                                # Plano de teste JMeter (+ Backend Listener)
├── docker-compose.yml                           # Sobe InfluxDB + Grafana
├── Jmeter_Jenkins_Grafana.mp4                   # Vídeo demonstrando o job em execução
├── README.md                                    # Este arquivo
└── grafana/
    ├── provisioning/
    │   ├── datasources/influxdb.yml             # Datasource InfluxDB (auto)
    │   └── dashboards/dashboards.yml            # Provider de dashboards (auto)
    └── dashboards/
        └── jmeter-load-test.json                # Dashboard JMeter (auto)
```

O plano de teste ([`test-plan.jmx`](test-plan.jmx)) dispara dois HTTP
samplers (`/` e `/reserve.php`) contra o **blazedemo.com**, com um
`Constant Throughput Timer` e parametrização via propriedades
(`host`, `threads`, `ramp`, `duration`, `influxUrl`).

---

## 3. Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
  (ou Docker Engine + Compose v2) instalado e em execução.
- Portas livres no host: **8080** (Jenkins), **8086** (InfluxDB) e
  **3000** (Grafana).

> Não é necessário ter Java ou JMeter instalados localmente -
> tudo já está empacotado na imagem `ac-jenkins`.

---

## 4. Subir a infraestrutura (ordem importa!)

### 4.1. Primeiro o Jenkins (cria a rede Docker compartilhada)

```powershell
# A partir da raiz do repositório
cd docker
docker compose up -d --build
```

Isso cria a rede `docker_default`, usada também pelo InfluxDB e Grafana.

### 4.2. Depois o InfluxDB + Grafana

```powershell
cd ../job/JMeter_Jenkins_Grafana
docker compose up -d
```

> O `docker-compose.yml` deste job conecta o InfluxDB e o Grafana à rede
> externa `docker_default`. Assim, o JMeter (que roda **dentro** do
> contêiner `ac-jenkins`) consegue resolver o host `influxdb`.
>
> Se o seu Compose nomear a rede de forma diferente, descubra com
> `docker network ls` e ajuste o campo `networks.jenkins_net.name` no
> [`docker-compose.yml`](docker-compose.yml).

| Serviço   | URL                     | Login         |
|-----------|-------------------------|---------------|
| Jenkins   | http://localhost:8080   | `admin` / `admin` |
| Grafana   | http://localhost:3000   | `admin` / `admin` |
| InfluxDB  | http://localhost:8086   | (sem auth)    |

O datasource e o dashboard **AC - JMeter Load Test** já vêm provisionados
no Grafana automaticamente (pasta *JMeter*).

---

## 5. Criar o job no Jenkins

1. Na UI do Jenkins, clique em **New Item**.
2. Nome: `jmeter-jenkins-grafana` -> tipo **Pipeline** -> **OK**.
3. Na seção **Pipeline**:
   - **Definition**: `Pipeline script from SCM` *(se você tiver o repo
     no Git)*
     - **SCM**: Git -> **Repository URL**: URL do seu fork/clone
     - **Script Path**: `job/JMeter_Jenkins_Grafana/Jenkinsfile`
   - **OU** escolha **Pipeline script** e cole o conteúdo de
     [`Jenkinsfile`](Jenkinsfile) (o repositório está montado em
     `/workspace`).
4. **Save** -> **Build with Parameters**.

### Parâmetros do build

| Parâmetro  | Padrão          | Descrição                          |
|------------|-----------------|------------------------------------|
| `HOST`     | `blazedemo.com` | Host alvo do teste de carga        |
| `THREADS`  | `10`            | Usuários virtuais simultâneos      |
| `RAMP`     | `5`             | Ramp-up (segundos)                 |
| `DURATION` | `60`            | Duração do teste (segundos)        |

---

## 6. Acompanhar os resultados

- **Em tempo real (Grafana):** abra http://localhost:3000 -> dashboard
  **AC - JMeter Load Test** enquanto o build roda. Os painéis mostram
  usuários ativos, throughput, tempos de resposta (média e percentis
  p90/p95/p99) e requisições OK vs KO.
- **Relatório do build (Jenkins):** ao final, o pipeline publica:
  - **JMeter HTML Report** - dashboard estático nativo do JMeter.
  - **Performance Trend** - gráfico de tendência entre builds (plugin
    `performance`).
  - O arquivo `results/results.jtl` é arquivado como artefato.

Plugins obrigatórios da AC:
- **Chuck Norris** - step `chuckNorris()` no bloco `post { always }`.
- **EZ Wall** - plugin de *view*; já fica em uso assim que instalado.

---

## 7. Rodar o JMeter localmente sem Jenkins (opcional)

```powershell
# Requer JMeter 5.6+ e os contêineres InfluxDB/Grafana no ar.
# Fora do contêiner, use localhost no lugar de "influxdb".
cd job/JMeter_Jenkins_Grafana
jmeter -n -t test-plan.jmx -l results/results.jtl -e -o report `
  -Jthreads=10 -Jramp=5 -Jduration=60 `
  -JinfluxUrl="http://localhost:8086/write?db=jmeter"
```

---

## 8. Parar / limpar o ambiente

```powershell
# Stack de monitoramento (InfluxDB + Grafana)
cd job/JMeter_Jenkins_Grafana
docker compose down            # para os contêineres
docker compose down -v         # também apaga os dados do InfluxDB/Grafana

# Jenkins
cd ../../docker
docker compose down
```

---

## 9. Jobs relacionados

A mesma imagem `ac-jenkins` é usada pelos outros jobs da AC:

- `job/Selenium_WebDriver_Jenkins/` - teste de UI com Selenium WebDriver.
- `job/JUnit_Jenkins/` - testes unitários puros com JUnit.
