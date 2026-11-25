const int PINO_LIKE = 2;
const int PINO_NEXT = 4;
const int PINO_PLAY_PAUSE = 12;
const int PINO_PREVIOUS = 13;

const int PINO_LED_RED = 5;
const int PINO_LED_GREEN = 9;
const int PINO_LED_BLUE = 6;


const int PINO_LDR = A0;

const unsigned long TEMPO_DEBOUNCE = 50;
const int BRILHO_MINIMO = 35;
const int BRILHO_MAXIMO = 255;

bool reproduzindo = false;

struct Botao {
    int pino;
    int estadoEstavel;
    int ultimaLeitura;
    unsigned long ultimoDebounce;
};

Botao botaoLike = {PINO_LIKE, HIGH, HIGH, 0};
Botao botaoNext = {PINO_NEXT, HIGH, HIGH, 0};
Botao botaoPlayPause = {PINO_PLAY_PAUSE, HIGH, HIGH, 0};
Botao botaoPrevious = {PINO_PREVIOUS, HIGH, HIGH, 0};

void enviarComando(const char* comando) {
    Serial.print("COMANDO: ");
    Serial.println(comando);
}

bool botaoPressionado(Botao &botao) {

    int leituraAtual = digitalRead(botao.pino);

    if (leituraAtual != botao.ultimaLeitura) {
        botao.ultimoDebounce = millis();
    }

    if ((millis() - botao.ultimoDebounce) >= TEMPO_DEBOUNCE) {

        if (leituraAtual != botao.estadoEstavel) {

            botao.estadoEstavel = leituraAtual;

            if (botao.estadoEstavel == LOW) {
                Serial.print("BOTAO DETECTADO - PINO: ");
                Serial.println(botao.pino);

                botao.ultimaLeitura = leituraAtual;
                return true;
            }
        }
    }

    botao.ultimaLeitura = leituraAtual;
    return false;
}

void atualizarLED() {

    int leituraLDR = analogRead(PINO_LDR);

    int brilho = map(
        leituraLDR,
        0,
        1023,
        BRILHO_MINIMO,
        BRILHO_MAXIMO
    );

    brilho = constrain(
        brilho,
        BRILHO_MINIMO,
        BRILHO_MAXIMO
    );

    if (reproduzindo) {
        analogWrite(PINO_LED_RED, 0);
        analogWrite(PINO_LED_BLUE, 0);
        analogWrite(PINO_LED_GREEN, brilho);

    } else {
        analogWrite(PINO_LED_RED, brilho);
        analogWrite(PINO_LED_GREEN, 0);
        analogWrite(PINO_LED_BLUE, 0);
    }
}

void setup() {

    Serial.begin(9600);

    pinMode(PINO_LIKE, INPUT_PULLUP);
    pinMode(PINO_NEXT, INPUT_PULLUP);
    pinMode(PINO_PLAY_PAUSE, INPUT_PULLUP);
    pinMode(PINO_PREVIOUS, INPUT_PULLUP);

    pinMode(PINO_LED_RED, OUTPUT);
    pinMode(PINO_LED_GREEN, OUTPUT);
    pinMode(PINO_LED_BLUE, OUTPUT);

    botaoLike.estadoEstavel = digitalRead(PINO_LIKE);
    botaoLike.ultimaLeitura = botaoLike.estadoEstavel;

    botaoNext.estadoEstavel = digitalRead(PINO_NEXT);
    botaoNext.ultimaLeitura = botaoNext.estadoEstavel;

    botaoPlayPause.estadoEstavel = digitalRead(PINO_PLAY_PAUSE);
    botaoPlayPause.ultimaLeitura = botaoPlayPause.estadoEstavel;

    botaoPrevious.estadoEstavel = digitalRead(PINO_PREVIOUS);
    botaoPrevious.ultimaLeitura = botaoPrevious.estadoEstavel;

    atualizarLED();

    Serial.println("================================");
    Serial.println("SPOTIFY CONTROLLER INICIADO");
    Serial.println("ARDUINO UNO R3");
    Serial.println("COMUNICACAO SERIAL: 9600");
    Serial.println("================================");
}

void loop() {
    if (botaoPressionado(botaoLike)) {
        enviarComando("LIKE");
    }

    if (botaoPressionado(botaoNext)) {
        enviarComando("NEXT");
    }

    if (botaoPressionado(botaoPlayPause)) {

        reproduzindo = !reproduzindo;

        if (reproduzindo) {
            enviarComando("PLAY");
        } else {
            enviarComando("PAUSE");
        }

        atualizarLED();
    }

    if (botaoPressionado(botaoPrevious)) {
        enviarComando("PREVIOUS");
    }

    atualizarLED();
}
