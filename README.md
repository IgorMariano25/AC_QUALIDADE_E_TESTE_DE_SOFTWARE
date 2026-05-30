# AC - Qualidade e Teste de Software

Repositório com os trabalhos da AC da disciplina **Qualidade e Teste
de Software**. São **3 jobs do Jenkins** independentes, cada um em sua
pasta, mas todos rodam sobre a **mesma** infraestrutura Docker
(`Dockerfile` + `docker-compose.yml`) localizada em [`docker/`](docker).

## Estrutura

```
.
├── docker/                              # Dockerfile + docker-compose compartilhados
│   ├── Dockerfile
│   ├── docker-compose.yml
│   ├── plugins.txt
│   ├── casc.yaml
│   └── README.md
└── job/
    ├── Selenium_WebDriver_Jenkins/      # Job 1 - Selenium WebDriver + JUnit 5
    ├── JUnit_Jenkins/                   # Job 2 - JUnit puro (a implementar)
    └── JMeter_Jenkins_Grafana/          # Job 3 - JMeter + Grafana (a implementar)
```

## Subir o Jenkins

```powershell
cd docker
docker compose up -d --build
```

Acesse **http://localhost:8080** (usuário `admin`, senha `admin`).

## Jobs

| # | Job | Status | Documentação |
|---|-----|--------|--------------|
| 1 | Selenium WebDriver + Jenkins | Implementado | [README](job/Selenium_WebDriver_Jenkins/README.md) |
| 2 | JUnit + Jenkins              | A implementar | [pasta](job/JUnit_Jenkins) |
| 3 | JMeter + Jenkins + Grafana   | A implementar | [pasta](job/JMeter_Jenkins_Grafana) |

Consulte o [README do Docker](docker/README.md) para detalhes da imagem
e do `docker-compose` compartilhados.

