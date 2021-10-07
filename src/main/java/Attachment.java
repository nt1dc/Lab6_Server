import messages.AnswerMsg;
import messages.CommandMsg;

import java.nio.channels.SocketChannel;

public class Attachment {
    private CommandMsg commandMsg;
    private AnswerMsg answerMsg;
    private SocketChannel socketChannel;

    public Attachment(CommandMsg commandMsg, AnswerMsg answerMsg, SocketChannel socketChannel) {
        this.commandMsg = commandMsg;
        this.answerMsg = answerMsg;
        this.socketChannel = socketChannel;
    }

    public CommandMsg getCommandMsg() {
        return commandMsg;
    }

    public void setCommandMsg(CommandMsg commandMsg) {
        this.commandMsg = commandMsg;
    }

    public AnswerMsg getAnswerMsg() {
        return answerMsg;
    }

    public void setAnswerMsg(AnswerMsg answerMsg) {
        this.answerMsg = answerMsg;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }
}
