package zeus.command;

import zeus.exception.ZeusException;
import zeus.main.Storage;
import zeus.main.TaskList;
import zeus.main.Ui;

/**
 * Class that handles the case of marking a Task as done.
 */

public class TaskDoneCommand extends Command {

    private int idx;

    /**
     * Constructor for TaskDoneCommand.
     *
     * @param idx
     */
    public TaskDoneCommand(int idx) {
        this.idx = idx;
    }

    /**
     * Executes the command to mark task as done.
     *
     * @param taskList List of tasks
     * @param ui The Ui
     * @param storage The Storage
     * @throws ZeusException If user input is invalid
     */
    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws ZeusException {
        if (this.idx < 0 || this.idx >= taskList.size()) {
            throw new ZeusException("☹ OOPS!!! Invalid index entered");
        }
        taskList.setTaskDone(this.idx);

        ui.generateLine();
        ui.printFormatted("Nice! I've marked this task as done:");
        System.out.println("\t   " + taskList.getTask(this.idx));
        ui.generateLine();

        storage.saveTasksToDisk(taskList.getTaskList());
    }
}
