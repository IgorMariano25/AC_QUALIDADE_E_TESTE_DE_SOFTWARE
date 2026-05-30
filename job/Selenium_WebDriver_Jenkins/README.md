# Selenium WebDriver + Jenkins

Job da AC de **Qualidade e Teste de Software** que executa um teste
automatizado de UI com **Selenium WebDriver 4** + **JUnit 5**, orquestrado
por um pipeline declarativo do **Jenkins** rodando em **Docker**.

---

## 1. O que este job faz

1. O Jenkins (contêiner `ac-jenkins`) clona este repositório na workspace
   do build (passo *Checkout SCM*).
2. O `Jenkinsfile` entra na pasta deste job e roda `mvn clean test`.
3. O teste [`DuckDuckGoSearchTest`](src/test/java/br/edu/ac/selenium/DuckDuckGoSearchTest.java)
   abre o Chrome em modo **headless**, pesquisa "Selenium WebDriver"
   no DuckDuckGo e valida que o título da página contém "selenium".
4. O relatório JUnit gerado em `target/surefire-reports/*.xml` é
   publicado na aba **Test Result** do build no Jenkins.
5. Plugins obrigatórios da AC:
   - **Chuck Norris** - chamado como step `chuckNorris()` no bloco
     `post { always { ... } }`, exibindo uma "fact" em cada build.
   - **EZ Wall** - é um plugin de *view* (mural/wall display). Ele **não**
     tem step de pipeline: assim que instalado, adiciona automaticamente
     o link de wall display a cada build/job, então já fica "em uso".

```
job/Selenium_WebDriver_Jenkins/
├── Jenkinsfile                                  # Pipeline declarativo
├── pom.xml                                      # Projeto Maven
├── README.md                                    # Este arquivo
└── src/test/java/br/edu/ac/selenium/
    └── DuckDuckGoSearchTest.java                # Teste Selenium + JUnit 5
```

---

## 2. Pré-requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
  (ou Docker Engine + Compose v2) instalado e em execução.
- Pelo menos **2 GB de RAM** livres para o contêiner do Jenkins.
- Portas **8080** e **50000** livres na máquina host.

> Não é necessário ter Java, Maven ou Chrome instalados localmente -
> tudo já está empacotado na imagem `ac-jenkins`.

---

## 3. Subir a infraestrutura Jenkins

Os arquivos de Docker são **compartilhados** pelos 3 jobs da AC e ficam
em [`docker/`](../../docker) na raiz do repositório.

```powershell
# A partir da raiz do repositório
cd docker
docker compose up -d --build
```

O primeiro build demora alguns minutos (baixa Chrome, Maven, JMeter e
plugins do Jenkins). Depois disso o contêiner sobe em segundos.

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
2. Nome: `selenium-webdriver-jenkins` -> tipo **Pipeline** -> **OK**.
3. Na seção **Pipeline**:
   - **Definition**: `Pipeline script from SCM` *(se você tiver o repo
     no Git)*
     - **SCM**: Git
     - **Repository URL**: URL do seu fork/clone
     - **Script Path**: `job/Selenium_WebDriver_Jenkins/Jenkinsfile`
   - **OU**, sem Git, escolha **Pipeline script** e cole o conteúdo de
     [`Jenkinsfile`](Jenkinsfile). Como o repositório está montado em
     `/workspace`, o pipeline já encontra o projeto.
4. **Save** -> **Build Now**.

---

## 5. Rodar localmente sem Jenkins (opcional)

Útil para depurar o teste antes de subir ao pipeline:

```powershell
# Dentro de job/Selenium_WebDriver_Jenkins
mvn clean test
```

Requer JDK 17, Maven 3.9+ e Google Chrome instalados no host.
O Selenium Manager baixa o `chromedriver` automaticamente.

---

## 6. Parar / limpar o ambiente

```powershell
cd docker
docker compose down            # para os contêineres
docker compose down -v         # também apaga o volume jenkins_home
```

---

## 7. Próximos jobs

A mesma imagem `ac-jenkins` será usada pelos outros dois jobs da AC:

- `job/JUnit_Jenkins/` - testes unitários puros com JUnit.
- `job/JMeter_Jenkins_Grafana/` - testes de carga com JMeter +
  dashboard no Grafana.
