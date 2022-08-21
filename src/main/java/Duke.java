import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Chatbot class that helps a person to keep track of various things.
 */
public class Duke {
    private boolean hasExited = false;
    private ArrayList<Task> taskList;

    /**
     * Constructor of chatbot class.
     */
    public Duke() {
        this.taskList = new ArrayList<>();
    }

    private void chat() {
        try {
            loadData();
        } catch (DukeException e) {
            generateLine();
            printFormatted(e.getMessage());
            generateLine();
        }
        greetUser();
        Scanner sc = new Scanner(System.in);
        while(!hasExited) {
            try {
                String input = sc.nextLine().trim();
                if (input.equals("bye")) {
                    hasExited = true;
                } else if (input.equals("list")) {
                    printList();
                } else if (input.startsWith("mark")) {
                    int taskNum = Integer.parseInt(input.replace("mark", "").trim());
                    markTaskAsDone(taskNum);
                } else if (input.startsWith("unmark")) {
                    int taskNum = Integer.parseInt(input.replace("unmark", "").trim());
                    markTaskAsNotDone(taskNum);
                } else if (input.startsWith("todo")) {
                    String t = input.replace("todo", "").trim();
                    if (t.isEmpty()) {
                        throw new DukeException("☹ OOPS!!! The description of a todo cannot be empty.");
                    }
                    addTodo(t);
                } else if (input.startsWith("deadline")) {
                    String[] deadlineInfo = input.replace("deadline ", "").split(" /by ");
                    addDeadline(deadlineInfo[0], deadlineInfo[1]);
                } else if (input.startsWith("event")) {
                    String[] eventInfo = input.replace("event ", "").split(" /at ");
                    addEvent(eventInfo[0], eventInfo[1]);
                } else if (input.startsWith("delete")) {
                    int deleteIdx = Integer.parseInt(input.replace("delete", "").trim());
                    deleteTask(deleteIdx);
                } else {
                    throw new DukeException("☹ OOPS!!! I'm sorry, but I don't know what that means :-(");
                }
                saveTasksToDisk();
            } catch (DukeException exception) {
                generateLine();
                printFormatted(exception.getMessage());
                generateLine();
            }
        }
        exitMessage();
    }

    private void saveTasksToDisk() throws DukeException {
        try {
            File f = new File("./data/duke.txt");
            File parent = f.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create dir: " + parent);
            }
            //FileWriter fw = new FileWriter("./data/duke.txt", true);
            BufferedWriter fw = new BufferedWriter(new FileWriter(f));
            for (Task t : this.taskList) {
                fw.append(t.getFileFormat());
                fw.append("\n");
            }
            fw.close();
        } catch (IOException e) {
            throw new DukeException("Error writing to file.");
        }
    }

    private void loadData() throws DukeException {
        try {
            File f = new File("./data/duke.txt");
            Scanner s = new Scanner(f);
            while (s.hasNext()) {
                String line = s.nextLine();
                String[] words = line.split(" \\| ");
                switch (words[0]) {
                case "T":
                    Todo t = new Todo(words[2]);
                    if (Integer.parseInt(words[1]) == 1) {
                        t.markAsDone();
                    }
                    this.taskList.add(t);
                    break;
                case "D":
                    Deadline d = new Deadline(words[2], words[3]);
                    if (Integer.parseInt(words[1]) == 1) {
                        d.markAsDone();
                    }
                    this.taskList.add(d);
                    break;
                case "E":
                    Event e = new Event(words[2], words[3]);
                    if (Integer.parseInt(words[1]) == 1) {
                        e.markAsDone();
                    }
                    this.taskList.add(e);
                    break;
                default:
                    throw new DukeException("☹ Could not read file.");
                }
            }
            s.close();
        } catch (IOException e) {
            throw new DukeException("☹ File not found or folder does not exist yet.");
        }
    }

    private void markTaskAsDone(int index) {
        Task currTask = this.taskList.get(index - 1);
        currTask.markAsDone();

        generateLine();
        printFormatted("Nice! I've marked this task as done:");
        System.out.println("\t   " + currTask);
        generateLine();
    }

    private void markTaskAsNotDone(int index) {
        Task currTask = this.taskList.get(index - 1);
        currTask.markAsNotDone();

        generateLine();
        printFormatted("OK, I've marked this task as not done yet:");
        System.out.println("\t   " + currTask);
        generateLine();
    }

    private void addTodo(String input) {
        Todo todo = new Todo(input);
        this.taskList.add(todo);
        printAddTask(todo);
    }

    private void addDeadline(String input, String by) {
        Deadline d = new Deadline(input, by);
        this.taskList.add(d);
        printAddTask(d);
    }

    private void addEvent(String input, String datetime) {
        Event event = new Event(input, datetime);
        this.taskList.add(event);
        printAddTask(event);
    }

    private void printAddTask(Task task) {
        generateLine();
        printFormatted("Got it. I've added this task:");
        printFormatted("  " + task);
        printFormatted("Now you have " + taskList.size() + " tasks in the list.");
        generateLine();
    }

    private void deleteTask(int deleteIdx) {
        Task taskToDelete = taskList.get(deleteIdx - 1);
        taskList.remove(deleteIdx - 1);
        generateLine();
        printFormatted("Noted. I've removed this task:");
        printFormatted("  " + taskToDelete);
        printFormatted("Now you have " + taskList.size() + " tasks in the list.");
        generateLine();
    }

    private void printList() {
        generateLine();
        printFormatted("Here are the tasks in your list:");
        for (int i = 0; i < this.taskList.size(); i++) {
            Task t =  this.taskList.get(i);
            String currLine = "\t " + (i + 1) + "." + t;
            System.out.println(currLine);
        }
        generateLine();
    }

    private void greetUser() {
        generateLine();
        printFormatted("Hello! I'm Zeus");
        printFormatted("What can I do for you?");
        generateLine();
    }

    private void exitMessage() {
        generateLine();
        printFormatted("Bye. Hope to see you again soon!");
        generateLine();
    }

    private void generateLine() {
        System.out.println("\t____________________________________________________________");
    }

    private void printFormatted(String message) {
        System.out.println("\t " + message);
    }

    /**
     * Main method that initialises chatbot and starts the chat.
     *
     * @param args a String array of input arguments
     */
    public static void main(String[] args) {
        Duke zeus = new Duke();
        zeus.chat();
    }
}
