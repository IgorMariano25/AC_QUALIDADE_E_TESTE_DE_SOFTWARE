# JUnit + Jenkins

Job da AC de **Qualidade e Teste de Software** que executa **testes unitários
puros** com **JUnit 5 (Jupiter)**, orquestrados por um pipeline declarativo do
**Jenkins** rodando em **Docker**.

Diferente do job Selenium (que testa a interface no navegador), aqui validamos
apenas a **lógica de negócio em memória** — testes rápidos, determinísticos e
sem dependências externas (rede, navegador ou banco de dados).

---

## 1. O que este job faz

1. O Jenkins (contêiner `ac-jenkins`) clona este repositório na workspace
   do build (passo *Checkout SCM*).
2. O `Jenkinsfile` entra na pasta deste job e roda `mvn clean test`.
3. O teste [`CalculadoraTest`](src/test/java/br/edu/ac/junit/CalculadoraTest.java)
   valida os métodos da classe
   [`Calculadora`](src/main/java/br/edu/ac/junit/Calculadora.java): soma,
   subtração, multiplicação, divisão (incluindo divisão por zero) e checagem
   de número par/ímpar — usando `@Test`, `@ParameterizedTest`, `@CsvSource`
   e `@ValueSource`.
4. O relatório JUnit gerado em `target/surefire-reports/*.xml` é
   publicado na aba **Test Result** do build no Jenkins.
5. Plugins obrigatórios da AC:
   - **Chuck Norris** - chamado como step `chuckNorris()` no bloco
     `post { always { ... } }`, exibindo uma "fact" em cada build.
   - **EZ Wall** - é um plugin de *view* (mural/wall display). Ele **não**
     tem step de pipeline: assim que instalado, adiciona automaticamente
     o link de wall display a cada build/job, então já fica "em uso".

```
job/JUnit_Jenkins/
├── Jenkinsfile                                  # Pipeline declarativo
├── pom.xml                                      # Projeto Maven
├── README.md                                    # Este arquivo
├── src/main/java/br/edu/ac/junit/
│   └── Calculadora.java                         # Classe de domínio testada
└── src/test/java/br/edu/ac/junit/
    └── CalculadoraTest.java                     # Testes unitários JUnit 5
```

---

## 2. Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
  (ou Docker Engine + Compose v2) instalado e em execução.
- Pelo menos **2 GB de RAM** livres para o contêiner do Jenkins.
- Portas **8080** e **50000** livres na máquina host.

> Não é necessário ter Java ou Maven instalados localmente -
> tudo já está empacotado na imagem `ac-jenkins`.

---

## 3. Subir a infraestrutura Jenkins

Os arquivos de Docker são **compartilhados** pelos jobs da AC e ficam
em [`docker/`](../../docker) na raiz do repositório.

```powershell
# A partir da raiz do repositório
cd docker
docker compose up -d --build
```

O primeiro build demora alguns minutos (baixa Maven, plugins do Jenkins,
etc.). Depois disso o contêiner sobe em segundos.

Acesse a UI em **http://localhost:8080** e faça login com:

| Usuário | Senha |
|---------|-------|
| `admin` | `admin` |

> Credenciais definidas em [`docker/docker-compose.yml`](../../docker/docker-compose.yml)
> e aplicadas pelo Jenkins Configuration as Code
> ([`docker/casc.yaml`](../../docker/casc.yaml)).

---

## 4. Criar o job no Jenkins

1. Na UI do Jenkins, clique em **New Item**.
2. Nome: `junit-jenkins` -> tipo **Pipeline** -> **OK**.
3. Na seção **Pipeline**:
   - **Definition**: `Pipeline script from SCM` *(se você tiver o repo
     no Git)*
     - **SCM**: Git
     - **Repository URL**: URL do seu fork/clone
     - **Script Path**: `job/JUnit_Jenkins/Jenkinsfile`
   - **OU**, sem Git, escolha **Pipeline script** e cole o conteúdo de
     [`Jenkinsfile`](Jenkinsfile). Como o repositório está montado em
     `/workspace`, o pipeline já encontra o projeto.
4. **Save** -> **Build Now**.

---

## 5. Rodar localmente sem Jenkins (opcional)

Útil para depurar os testes antes de subir ao pipeline:

```powershell
# Dentro de job/JUnit_Jenkins
mvn clean test
```

Requer JDK 17 e Maven 3.9+ instalados no host. O Maven baixa as
dependências do JUnit automaticamente.

Saída esperada (todos os testes verdes):

```
[INFO] Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 6. Parar / limpar o ambiente

```powershell
cd docker
docker compose down            # para os contêineres
docker compose down -v         # também apaga o volume jenkins_home
```

---

## 7. Jobs relacionados

A mesma imagem `ac-jenkins` é usada pelos outros jobs da AC:

- `job/Selenium_WebDriver_Jenkins/` - teste de UI com Selenium WebDriver.
- `job/JMeter_Jenkins_Grafana/` - testes de carga com JMeter +
  dashboard no Grafana.
