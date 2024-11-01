package br.edu.iftm.tspi.sd.websockets_exemplo.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class TextoWebSocketHandler extends TextWebSocketHandler {

    private Thread messageThread;
    private volatile boolean keepSendingMessages = true;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Conexão estabelecida: " + session.getId());

        // Iniciar a Thread para enviar mensagens a cada 5 segundos
        messageThread = new Thread(() -> {
            while (keepSendingMessages && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage("Mensagem teste"));
                    Thread.sleep(5000); // Pausa de 5 segundos
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restaura o estado de interrupção
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        messageThread.start();
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Mensagem recebida: " + payload);
        session.sendMessage(new TextMessage("Mensagem recebida no servidor: " + payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Conexão fechada: " + session.getId());

        // Parar a Thread quando a conexão for fechada
        keepSendingMessages = false;
        if (messageThread != null && messageThread.isAlive()) {
            messageThread.interrupt();
        }
    }
}
