import messages.AnswerMsg;
import messages.CommandMsg;
import messages.Status;
import messages.User;

import java.io.*;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.*;


public class Commander implements Runnable {
    private final ArrayList<String> execute_Files = new ArrayList<>();
    Attachment attachment;
    private CollectionManager manager;
    private String userCommand;
    private Boolean run;
    private final String[] history = new String[6];
    private String[] finalUserCommand;
    private SelectionKey selectionKey;
    private AnswerMsg answerMsg;
    private CommandMsg commandMsg;
    private User user;
    private Queue<Attachment> sendQue;

    {
        userCommand = "";
    }


    public Commander(Attachment attachment, Queue<Attachment> sendQue, CollectionManager collectionManager) throws IOException {
        manager = collectionManager;
        this.attachment = attachment;
        answerMsg = attachment.getAnswerMsg();
        commandMsg = attachment.getCommandMsg();
        this.user = attachment.getCommandMsg().getUser();
        this.sendQue = sendQue;

    }


    /**
     * Хрень которая лишила меня сна 19.05.2021-20.05.2021
     * а в дальнейшем мб и вуза =/
     */
    /**
     * а может быть и нет, я вот сейчас пишу эту 7 лабу с 10 часов утра, сейчас уже 2 часа ночи. И понимаю какой я тупой, но это все прекольно.
     * Надеюсь не зря писал
     */

    private void execute_script(CommandMsg command, AnswerMsg ans) {
        if (execute_Files.contains(command.getCommandStringArgument())) {
            ans.AddErrorMsg("Looping " + "\n" +
                    "Going to intensive mod ");
            ans.AddStatus(Status.ERROR);
            execute_Files.clear();

        } else {
            run = true;
            File file = new File(command.getCommandStringArgument());
            try (Scanner exScanner = new Scanner(file)) {
                execute_Files.add(command.getCommandStringArgument());

//                CommandMsg commandMsg = new CommandMsg(exScanner.nextLine().trim().split(" ", 2)[0], exScanner.nextLine().trim().split(" ", 2)[1], studyGroup);
                while (exScanner.hasNextLine() && !finalUserCommand[0].equals("exit")) {
                    GroupBuilder groupBuilder = new GroupBuilder(exScanner);
                    StudyGroup studyGroup = null;
                    userCommand = exScanner.nextLine();
                    finalUserCommand = userCommand.trim().split(" ", 2);
                    CommandMsg commandMsg = new CommandMsg(finalUserCommand[0], "", studyGroup, user);
                    try {
                        if (finalUserCommand[0].equals("update") || finalUserCommand[0].equals("remove_by_id") || finalUserCommand[0].equals("") || finalUserCommand[0].equals("execute_script") || finalUserCommand[0].equals("remove_all_by_form_of_education") || finalUserCommand[0].equals("count_by_semester_enum")) {
                            commandMsg.setCommandStringArgument(finalUserCommand[1]);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        ans.AddErrorMsg("No argument");
                        ans.AddStatus(Status.ERROR);
                    }

                    if (commandMsg.getCommandName().equals("add") | commandMsg.getCommandName().equals("add_if_min") | commandMsg.getCommandName().equals("update")) {
                        groupBuilder.setFields();
                        commandMsg.setCommandObjectArgument(groupBuilder.studyGropCreator());
                    }
                    if (execute_Files.contains(commandMsg.getCommandStringArgument()) & commandMsg.getCommandName().equals("execute_script")) {
                        ans.AddErrorMsg("Looping " + "\n" +
                                "Going to intensive mod ");
                        ans.AddStatus(Status.ERROR);
                        execute_Files.clear();
                        run = false;
                        break;
                    } else {
                        start(commandMsg, ans);
                    }
                }
            } catch (
                    FileNotFoundException | SQLException e) {
                ans.AddStatus(Status.ERROR);
                ans.AddErrorMsg("File not found");
            } finally {
                ans.AddAnswer("Script done");
            }
        }

    }

    /**
     * Gets command and use them
     */
    public void start(CommandMsg commandMsg, AnswerMsg ans) throws SQLException {
        if (manager.databaseManager.checkUser(commandMsg.getUser().getUserName(), commandMsg.getUser().getPassword())) {
            ans.AddStatus(Status.FINE);
            System.out.println("still valid");
        } else {
            ans.AddStatus(Status.EXIT);
            ans.AddErrorMsg("WTF??????????");
        }
        finalUserCommand = commandMsg.getCommandName().trim().split(" ", 2);
        try {
            history[5] = this.finalUserCommand[0];
            for (int i = 0; i < 5; i++) {
                history[i] = history[i + 1];
            }
            switch (this.finalUserCommand[0]) {
                case "":
                    break;
                case "help":
                    manager.help(ans);
                    break;
                case "info":
                    manager.info(ans);
                    break;
                case "add":
                    manager.add((StudyGroup) commandMsg.getCommandObjectArgument(), ans, attachment);
                    break;
                case "show":
                    manager.show(ans);
                    break;
                case "update":
                    manager.update(commandMsg.getCommandObjectArgument(), ans, commandMsg.getCommandStringArgument());
                    break;
                case "clear":
                    manager.clear(ans);
                    break;
//                case "save":
//                    manager.save(ans);
//                    break;
                case "remove_by_id":
                    manager.remove_by_id(commandMsg.getCommandStringArgument(), ans,commandMsg.getUser());
                    break;

                case "history":
                    ans.AddAnswer("Last 5 commands: \n");
                    for (int i = 0; i < 5; i++) {
                        if (history[i] != null) {
                            ans.AddAnswer(history[i]);
                        }
                    }
                    break;
                case "remove_all_by_form_of_education":
                    manager.remove_all_by_form_of_education(commandMsg.getCommandStringArgument(), ans);
                    break;
                case "count_by_semester_enum":
                    manager.count_by_semester_enum(commandMsg.getCommandStringArgument(), ans);
                    break;
                case "add_if_min":
                    manager.add_if_min((StudyGroup) commandMsg.getCommandObjectArgument(), ans);
                    break;
                case "remove_head":
                    manager.remove_head(ans);
                    break;
                case "execute_script":
                    try {
                        execute_script(commandMsg, ans);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "print_unique_semester_enum":
                    manager.print_unique_semester_enum(ans);
                    break;

                case "exit":
                    if (finalUserCommand[0].equals("exit")) {
                        ans.AddAnswer("Exit");
                        ans.AddStatus(Status.EXIT);
                    }
                    break;
                default:

                    ans.AddAnswer("Unknown command. Use 'help' for help.");
                    for (int i = 5; i > 0; i--) {
                        history[i] = history[i - 1];
                    }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            ans.AddAnswer("No argument ex");
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commander)) return false;
        Commander commander = (Commander) o;
        return Objects.equals(manager, commander.manager);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(manager, userCommand);
        result = 31 * result + Arrays.hashCode(finalUserCommand);
        return result;
    }

    public AnswerMsg getAnswerMsg() {
        return answerMsg;
    }

    @Override
    public void run() {
        attachment.getAnswerMsg().AddStatus(Status.FINE);
        if (commandMsg.getCommandName().equals("registration")) {
            try {
                manager.databaseManager.register(commandMsg.getUser().getUserName(), commandMsg.getUser().getPassword());
                attachment.getAnswerMsg().AddStatus(Status.FINE);
            } catch (SQLException throwables) {
                answerMsg.AddStatus(Status.ERROR);
                answerMsg.AddErrorMsg("Такой юзер уже имеется");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (commandMsg.getCommandName().equals("authentication")) {
            try {

                if (manager.databaseManager.checkUser(attachment.getCommandMsg().getUser().getUserName(), attachment.getCommandMsg().getUser().getPassword())) {
                    Main.logger.info("User " + user.getUserName() + " has authorized in db");
                    attachment.getAnswerMsg().setMsg("Хорошая работа ОЛЕГ");
                } else {
                    answerMsg.AddErrorMsg("");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                attachment.getAnswerMsg().AddStatus(Status.ERROR);
                attachment.getAnswerMsg().AddErrorMsg("Ошибка при работе с БД");
            }
        } else {
            try {
                if (manager.databaseManager.checkUser(attachment.getCommandMsg().getUser().getUserName(), attachment.getCommandMsg().getUser().getPassword())) {
                    start(commandMsg, answerMsg);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        sendQue.add(attachment);
    }

}
