import messages.AnswerMsg;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main class for serverSocketChannel, connect and send
 */
public class Server {
    Queue<Attachment> executeQue = new PriorityQueue<>();
    Queue<Attachment> sendQue = new PriorityQueue<>();
    private final int port;
    private final int timeout;
    private ServerSocketChannel serverSocketChannel;


    //    private ConnectionAccepter connectionAccepter;
    private Iterator<SelectionKey> iterator;


    public Server(int in_port, int in_timeout) throws IOException {
        port = in_port;
        timeout = in_timeout;
    }

    /**
     * Open and set serverSocketChannel
     *
     * @return All right or not
     */
    private boolean openSocket(Selector selector) {
        try {
            Main.logger.info("Начинаю запуск сервера");
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            Main.logger.info("Сервер успешно запущен");
            return true;
        } catch (IllegalArgumentException exception) {
            Main.logger.fatal("Порт '" + port + "' находится за пределами возможных значений");
            return false;
        } catch (IOException exception) {
            exception.printStackTrace();
            Main.logger.fatal("Произошла ошибка при попытке использовать порт");
            return false;
        }
    }

    /**
     * Close serverSocketChannel in end of work
     */
    private void closeSocket() {
        try {
            Main.logger.info("Пытаюсь закрыть сервер");
            serverSocketChannel.close();
            Main.logger.info("Сервер успешно закрыт");
        } catch (IOException exception) {
            Main.logger.error("Ошибка при закрытии сервера");
        }
    }


    private static boolean flag = true;


    /**
     * End transmission with current user if error happened (need wait next)
     */
//    private void endTransmission() {
//        try {
//            Main.logger.info("Закрываю соединение");
//            client.close();
//            ois.close();
//            ous.close();
//            Main.logger.info("Соединение успешно закрыто");
//        } catch (IOException exception) {
//            Main.logger.error("Ошибка закрытия соеденения");
//        }
//    }

    /**
     * Main run class, read client, execute and answer
     */

    public void run() throws IOException, ClassNotFoundException {
        CollectionManager collectionManager = new CollectionManager();
        collectionManager.load();
//        try {
////            collectionManager.databaseManager.register("АНТОХА","S");
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
        Selector selector = Selector.open();
        openSocket(selector);
        boolean work = true;
        ExecutorService read = Executors.newCachedThreadPool();
        ExecutorService send = Executors.newCachedThreadPool();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        SocketChannel client = null;
        Stack<SelectionKey> processingSelectionKeys = new Stack<>();


        while (true) {
            try {
//            int readyChannels = selector.select();
                if (selector.selectNow() == 0) {
                    Set<SelectionKey> readyKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = readyKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector, SelectionKey.OP_READ);
                            Main.logger.info("Some bitard has connected with address" + socketChannel.getRemoteAddress());
                        } else if (key.isReadable()) {
                            Iterator iterator1 = processingSelectionKeys.iterator();
                            boolean keyIsAlreadyInwork = false;

                            while (iterator1.hasNext()) {
                                if (key.equals(iterator1.next())) {
                                    keyIsAlreadyInwork = true;
                                    break;
                                }
                            }
                            if (!keyIsAlreadyInwork) {
                                SocketChannel socketChannel = (SocketChannel) key.channel();
                                processingSelectionKeys.add(key);
                                read.execute(new Reader(key, socketChannel, executeQue));
                                processingSelectionKeys.remove(key);
                            }
                        }
                    }
                }
                while (!executeQue.isEmpty()) {
                    Attachment attachment = executeQue.poll();
                    executor.execute(new Commander(attachment, sendQue,collectionManager));
                }
                while (!sendQue.isEmpty()) {
                    Attachment attachment = sendQue.poll();
                    send.execute(new Sender(attachment));
                }
            } catch (CancelledKeyException e) {
            }
        }
    }
}


//
//        CommandMsg commandMsg = readObj();
//        AnswerMsg answerMsg = new AnswerMsg();
////            System.out.println(commandMsg.getCommandObjectArgument().getClass());
//        commander.start(commandMsg, answerMsg);
//        if (!sendAnswer(answerMsg)) {
//            endTransmission();
//        }
//        if (answerMsg.getStatus() == Status.EXIT)
//            flag = false;
//
//        Main.logger.info("Конец завершение работы");
//        Main.logger.info("Сохранение коллекции");
//        AnswerMsg answerMsg = new AnswerMsg();
//        //commandManager.executeCommand(new CommandMsg("save", null, null), answerMsg);
//        Main.logger.info("Сохранание прошло со следующим сообщением: " + answerMsg.getMessage().
//
//                trim());
//
//        endTransmission();
//
//        closeSocket();



