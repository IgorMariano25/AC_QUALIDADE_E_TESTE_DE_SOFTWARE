# =====================================================================
# Imagem Jenkins customizada para os 3 jobs:
#   1) Selenium WebDriver + Jenkins
#   2) JUnit + Jenkins
#   3) JMeter + Jenkins + Grafana
#
# Base: Jenkins LTS com JDK 17 (oficial)
# Adiciona: Maven, Google Chrome + Chromedriver, JMeter, Git
# =====================================================================
FROM jenkins/jenkins:lts-jdk17

USER root

ARG MAVEN_VERSION=3.9.16
ARG JMETER_VERSION=5.6.3

ENV DEBIAN_FRONTEND=noninteractive \
    MAVEN_HOME=/opt/maven \
    JMETER_HOME=/opt/jmeter \
    PATH=/opt/maven/bin:/opt/jmeter/bin:$PATH

# ---------- Dependências de sistema ----------
RUN apt-get update && apt-get install -y --no-install-recommends \
        wget curl unzip gnupg ca-certificates fonts-liberation \
        libnss3 libxss1 libasound2 libatk-bridge2.0-0 libgtk-3-0 \
        libgbm1 xvfb git \
    && rm -rf /var/lib/apt/lists/*

# ---------- Google Chrome (estável) ----------
RUN wget -q -O /tmp/google-chrome.deb \
        https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get update \
    && apt-get install -y --no-install-recommends /tmp/google-chrome.deb \
    && rm -f /tmp/google-chrome.deb \
    && rm -rf /var/lib/apt/lists/*

# Selenium Manager (4.6+) baixa o driver correto automaticamente,
# então não precisamos fixar um chromedriver aqui.

# ---------- Maven ----------
# -4 força IPv4 (o bridge do Docker frequentemente não tem IPv6, e os
# hosts da Apache resolvem para IPv6, o que causa falha instantânea).
RUN set -eux; \
    MVN_TGZ=apache-maven-${MAVEN_VERSION}-bin.tar.gz; \
    (wget -4 --tries=3 --timeout=30 -q -O /tmp/maven.tgz \
        https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/${MVN_TGZ} \
     || wget -4 --tries=3 --timeout=30 -q -O /tmp/maven.tgz \
        https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/${MVN_TGZ}); \
    tar -xzf /tmp/maven.tgz -C /opt; \
    mv /opt/apache-maven-${MAVEN_VERSION} ${MAVEN_HOME}; \
    rm /tmp/maven.tgz

# ---------- JMeter ----------
RUN set -eux; \
    JM_TGZ=apache-jmeter-${JMETER_VERSION}.tgz; \
    (wget -4 --tries=3 --timeout=30 -q -O /tmp/jmeter.tgz \
        https://dlcdn.apache.org/jmeter/binaries/${JM_TGZ} \
     || wget -4 --tries=3 --timeout=30 -q -O /tmp/jmeter.tgz \
        https://archive.apache.org/dist/jmeter/binaries/${JM_TGZ}); \
    tar -xzf /tmp/jmeter.tgz -C /opt; \
    mv /opt/apache-jmeter-${JMETER_VERSION} ${JMETER_HOME}; \
    rm /tmp/jmeter.tgz

# ---------- Plugins do Jenkins instalados na primeira execução ----------
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

# Pula o wizard inicial (o admin é definido pelo docker-compose)
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

USER jenkins
