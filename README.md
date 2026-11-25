# Spotify Controller

> O Spotify Controller é um projeto de integração entre hardware e software que permite controlar a reprodução de músicas do Spotify por meio de uma interface física. Desenvolvido com Arduino Uno R3 e um backend em Java 21 com Spring Boot, o sistema utiliza botões físicos para controlar a reprodução, avançar e retornar faixas e curtir músicas. Além disso, conta com um sensor de luminosidade LDR, responsável por detectar a iluminação do ambiente e ajustar automaticamente o brilho do LED RGB, que também indica visualmente o estado da reprodução. A comunicação entre o Arduino e o backend ocorre por meio da porta serial USB, enquanto a integração com o Spotify é realizada através da Spotify Web API, utilizando autenticação OAuth 2.0.

## Tecnologias utilizadas

<div align="center">

[![Arduino](https://img.shields.io/badge/Arduino-Uno_R3-00979D?style=for-the-badge&logo=arduino&logoColor=white)](https://www.arduino.cc/)
[![C++](https://img.shields.io/badge/C%2B%2B-Arduino-00599C?style=for-the-badge&logo=cplusplus&logoColor=white)](https://isocpp.org/)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spotify](https://img.shields.io/badge/Spotify-Web_API-1DB954?style=for-the-badge&logo=spotify&logoColor=white)](https://developer.spotify.com/documentation/web-api)
[![jSerialComm](https://img.shields.io/badge/jSerialComm-2.11.4-blue?style=for-the-badge)](https://fazecast.github.io/jSerialComm/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![OAuth 2.0](https://img.shields.io/badge/OAuth_2.0-Auth-000000?style=for-the-badge)](https://oauth.net/2/)

</div>

## Visão geral

O projeto é dividido em dois componentes principais:

| Componente | Tecnologia | Responsabilidade |
|---|---|---|
| Controlador embarcado | Arduino Uno R3 e C++ | Realiza a leitura dos botões, coleta dados do sensor LDR, controla o LED RGB e envia comandos pela comunicação serial. |
| Aplicação backend | Java 21 e Spring Boot | Recebe os comandos do Arduino, gerencia a autenticação e realiza a integração com a Spotify Web API. |

O Arduino funciona como a interface física de controle, enquanto o backend é responsável pelo processamento dos comandos e pela comunicação com o Spotify.

## Funcionalidades

- Controle físico de reprodução e pausa das músicas.
- Avanço para a próxima faixa.
- Retorno para a faixa anterior.
- Curtir ou descurtir a música atual.
- Detecção da luminosidade ambiente por meio de sensor LDR.
- Ajuste automático do brilho do LED RGB conforme a iluminação do ambiente.
- Indicação visual do estado de reprodução por meio do LED RGB.
- Comunicação serial USB entre o Arduino e o backend Java.
- Autenticação da conta Spotify utilizando OAuth 2.0.
- Integração com a Spotify Web API.
- Implementação de debounce para evitar múltiplos acionamentos indesejados dos botões.

## Componentes de hardware

| Componente | Quantidade | Descrição |
|---|---:|---|
| Arduino Uno R3 | 1 | Microcontrolador principal do projeto. |
| Botões de pressão | 4 | Responsáveis pelos comandos físicos. |
| LED RGB | 1 | Indicador visual do estado de reprodução. |
| Sensor LDR | 1 | Responsável pela detecção da luminosidade ambiente. |
| Resistor de 10 kΩ | 1 | Utilizado no circuito divisor de tensão do LDR. |
| Resistores limitadores de corrente | 6 | Proteção dos canais do LED RGB. |
| Protoboard e jumpers | — | Montagem e conexão dos componentes. |
| Cabo USB | 1 | Alimentação e comunicação serial com o computador. |

## Mapeamento dos pinos

### Botões

Os botões utilizam a configuração `INPUT_PULLUP`, permitindo identificar o acionamento quando o pino digital é conectado ao GND.

| Função | Pino Arduino |
|---|---|
| Curtir música | D2 |
| Próxima música | D4 |
| Play / Pause | D12 |
| Música anterior | D13 |

### LED RGB

O LED RGB utiliza configuração de cátodo comum.

| Canal | Pino Arduino |
|---|---|
| Vermelho | D5 |
| Verde | D9 |
| Azul | D6 |

O LED apresenta as seguintes indicações:

| Estado da reprodução | Cor |
|---|---|
| Música pausada | Vermelho |
| Música reproduzindo | Verde |

A intensidade luminosa é ajustada de acordo com as leituras do sensor LDR.

### Sensor de luminosidade

| Componente | Pino Arduino |
|---|---|
| Sensor LDR | A0 |

O LDR é conectado por meio de um divisor de tensão utilizando um resistor de 10 kΩ. Dessa forma, o Arduino consegue realizar leituras analógicas da luminosidade ambiente.

## Arquitetura do sistema

O funcionamento do sistema segue o fluxo de comunicação entre hardware, backend e API externa:

```text
+-----------------------------+
|       Entradas físicas      |
|                             |
|    Botões + Sensor LDR      |
+--------------+--------------+
               |
               v
+-----------------------------+
|        Arduino Uno R3       |
|                             |
| - Leitura dos botões        |
| - Tratamento de debounce    |
| - Leitura do sensor LDR     |
| - Controle do LED RGB       |
| - Envio de comandos serial  |
+--------------+--------------+
               |
               | USB Serial
               | 9600 baud
               v
+-----------------------------+
|        Backend Java         |
|        Spring Boot          |
|                             |
| - SerialService             |
| - CommandProcessor          |
| - SpotifyAuthService        |
| - SpotifyService            |
+--------------+--------------+
               |
               | HTTPS / REST
               v
+-----------------------------+
|       Spotify Web API       |
|                             |
| - Controle de reprodução    |
| - Navegação entre músicas   |
| - Gerenciamento da biblioteca|
| - Consulta do estado atual  |
+-----------------------------+
```

## Comunicação serial

A comunicação entre o Arduino e o backend Java é realizada por meio da conexão USB, utilizando comunicação serial configurada em 9600 baud.

Quando um botão é pressionado, o Arduino envia uma mensagem de texto correspondente ao comando solicitado.

### Comandos disponíveis

| Comando | Descrição |
|---|---|
| `COMANDO: PLAY` | Solicita o início da reprodução. |
| `COMANDO: PAUSE` | Solicita a pausa da reprodução. |
| `COMANDO: NEXT` | Avança para a próxima música. |
| `COMANDO: PREVIOUS` | Retorna para a música anterior. |
| `COMANDO: LIKE` | Solicita a operação de curtida da música atual. |

O backend recebe as mensagens pela porta serial, identifica os comandos e os encaminha para os métodos responsáveis pela integração com o Spotify.

## Arquitetura do backend

A aplicação Spring Boot está organizada em pacotes separados conforme as responsabilidades de cada componente.

| Pacote | Responsabilidade |
|---|---|
| `arduino` | Armazena o código-fonte C++ utilizado no Arduino. |
| `serial` | Gerencia a comunicação serial USB utilizando jSerialComm. |
| `command` | Interpreta os comandos recebidos do Arduino e encaminha para os serviços correspondentes. |
| `auth` | Gerencia a autenticação OAuth 2.0 e os tokens de acesso do Spotify. |
| `spotify` | Contém a lógica de integração com a Spotify Web API. |

### Principais classes

- **SerialService:** estabelece a conexão serial, realiza a leitura dos dados enviados pelo Arduino e encaminha os comandos recebidos.
- **CommandProcessor:** interpreta os comandos recebidos e direciona cada operação ao serviço correspondente.
- **SpotifyAuthService:** gera a URL de autorização, valida o estado OAuth e realiza a troca do código de autorização pelos tokens de acesso.
- **SpotifyService:** executa as operações de reprodução, navegação entre músicas e gerenciamento da biblioteca do Spotify.

## Endpoints da aplicação

O backend disponibiliza endpoints HTTP para autenticação, controle da reprodução e consulta do estado atual do Spotify.

### Autenticação

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/spotify/login` | Inicia o processo de autenticação com o Spotify. |
| `GET` | `/callback` | Recebe o retorno da autorização e realiza a troca do código por tokens. |

### Reprodução

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/spotify/status` | Consulta o estado atual da reprodução. |
| `POST` | `/spotify/playback/toggle` | Alterna entre reprodução e pausa. |
| `POST` | `/spotify/playback/next` | Avança para a próxima música. |
| `POST` | `/spotify/playback/previous` | Retorna para a música anterior. |
| `POST` | `/spotify/playback/like` | Adiciona ou remove a música atual da biblioteca. |

## Autenticação e permissões

A integração com o Spotify utiliza OAuth 2.0 para permitir que a aplicação acesse os recursos autorizados da conta do usuário.

São utilizadas as seguintes permissões (*scopes*):

| Permissão | Finalidade |
|---|---|
| `user-modify-playback-state` | Permite controlar o estado da reprodução. |
| `user-read-playback-state` | Permite consultar o estado atual da reprodução. |
| `user-library-modify` | Permite adicionar e remover músicas da biblioteca. |
| `user-library-read` | Permite verificar se uma música já está salva na biblioteca. |

A aplicação deve ser registrada no [Spotify Developer Dashboard](https://developer.spotify.com/dashboard/).

## Como executar o projeto

### Pré-requisitos

- Arduino Uno R3.
- Arduino IDE.
- Java 21.
- Maven.
- Conta Spotify.
- Aplicação cadastrada no Spotify Developer Dashboard.
- Conexão USB entre o Arduino e o computador.

### 1. Montagem do hardware

1. Monte o circuito utilizando o mapeamento de pinos documentado neste README.
2. Conecte o Arduino ao computador por meio do cabo USB.
3. Abra o arquivo `code-arduino-cpp.ino` na Arduino IDE.
4. Selecione a placa Arduino Uno R3 e a porta serial correspondente.
5. Carregue o código-fonte para a placa.

### 2. Configuração das variáveis de ambiente

O backend utiliza variáveis de ambiente para armazenar as credenciais da aplicação Spotify:

| Variável | Descrição |
|---|---|
| `SPOTIFY_CLIENT_ID` | Identificador da aplicação Spotify. |
| `SPOTIFY_CLIENT_SECRET` | Chave secreta da aplicação Spotify. |
| `SPOTIFY_URL_REDIRECT` | URL de retorno configurada no Spotify Developer Dashboard. |

### 3. Configuração do backend

O arquivo `application.properties` utiliza as variáveis de ambiente:

```properties
spotify.client.id=${SPOTIFY_CLIENT_ID}
spotify.client.secret=${SPOTIFY_CLIENT_SECRET}
spotify.redirect.url=${SPOTIFY_URL_REDIRECT}
```

### 4. Configuração da porta serial

O backend deve utilizar a mesma porta serial atribuída ao Arduino.

Exemplo de configuração no Java:

```java
SerialPort serialPort = SerialPort.getCommPort("COM4");
```

Caso o dispositivo receba outra porta, o valor deve ser atualizado conforme o Gerenciador de Dispositivos do Windows.

O Monitor Serial da Arduino IDE deve permanecer fechado enquanto o backend estiver utilizando a mesma porta.

### 5. Executando o backend

Dentro do diretório `backend`, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

Caso o projeto não possua Maven Wrapper, utilize:

```powershell
mvn spring-boot:run
```

Após iniciar a aplicação:

1. Acesse `http://localhost:8080/spotify/login`.
2. Autorize a aplicação utilizando sua conta Spotify.
3. Confirme que o Arduino está conectado à porta serial configurada.
4. Utilize os botões físicos para controlar a reprodução.

## Estrutura do repositório

```text
spotify-controller/
│
├── arduino/
│   └── code-arduino-cpp.ino
│
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── rafaelvincensi/
│   │       │           └── backend/
│   │       │               ├── auth/
│   │       │               ├── command/
│   │       │               ├── serial/
│   │       │               └── spotify/
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
│
├── README.md
└── LICENSE
```

## Bibliotecas e dependências

| Dependência | Versão | Finalidade |
|---|---|---|
| Spring Boot | 4.1.2 | Framework utilizado no desenvolvimento do backend. |
| jSerialComm | 2.11.4 | Comunicação serial entre Java e Arduino. |
| Spring WebMVC | Dependência do projeto | Disponibilização dos endpoints HTTP e integração REST. |

O código do Arduino utiliza as funções padrão do ambiente Arduino e não necessita de bibliotecas externas adicionais na implementação atual.

---

<div align="center">

Built by [rafael-vincensi](https://github.com/rafael-vincensi) e Marcos Felipe Schmid

</div>
