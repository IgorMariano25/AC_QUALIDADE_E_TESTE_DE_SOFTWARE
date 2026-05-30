# Docker compartilhado - AC Qualidade e Teste de Software

Esta pasta contém **uma única** infraestrutura Docker usada pelos
**3 jobs** do Jenkins da AC:

| Job                                                   | Pasta                                                                 |
|-------------------------------------------------------|-----------------------------------------------------------------------|
| Selenium WebDriver + Jenkins                          | [`../job/Selenium_WebDriver_Jenkins`](../job/Selenium_WebDriver_Jenkins) |
| JUnit + Jenkins                                       | [`../job/JUnit_Jenkins`](../job/JUnit_Jenkins)                         |
| JMeter + Jenkins + Grafana                            | [`../job/JMeter_Jenkins_Grafana`](../job/JMeter_Jenkins_Grafana)       |

## Conteúdo

- [`Dockerfile`](Dockerfile) - imagem `ac-jenkins:latest` baseada em
  `jenkins/jenkins:lts-jdk17`, com **Maven**, **Google Chrome**,
  **JMeter** e os plugins do Jenkins já provisionados.
- [`docker-compose.yml`](docker-compose.yml) - sobe o serviço
  `jenkins` em `http://localhost:8080` e monta a raiz do repositório
  em `/workspace` dentro do contêiner.
- [`plugins.txt`](plugins.txt) - lista de plugins instalados no
  primeiro boot. Inclui os dois plugins obrigatórios da AC:
  **`chucknorris`** e **`ezwall`**, usados por todos os 3 jobs.
- [`casc.yaml`](casc.yaml) - Configuration as Code do Jenkins
  (cria o usuário `admin/admin` e desliga o setup wizard).

## Uso rápido

```powershell
cd docker
docker compose up -d --build      # primeira execução: ~5 min
# UI:  http://localhost:8080   (admin / admin)

docker compose logs -f jenkins    # acompanhar o boot
docker compose down               # parar
docker compose down -v            # parar e limpar o jenkins_home
```

Cada job tem o seu próprio `README.md` explicando como criar o
pipeline correspondente dentro do Jenkins.
