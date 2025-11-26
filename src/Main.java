import buffered.BufferingIO;
import buffered.Task;
import fileIO.FileIOImpl;
import streams.StreamAPI;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // ============= Streams Implementation ==============
        StreamAPI streamAPI = new StreamAPI();
        // streamAPI.streamAPI();

        Task task = new Task();
        // task.task();

        // ============ Buffered IO ================
        BufferingIO bufferingIO = new BufferingIO();
        // bufferingIO.bufferingIOImpl();

        buffered.Task bufferedTask = new Task();
        // bufferedTask.task();

        // ============= File IO ==============

        FileIOImpl fileIO = new FileIOImpl();
        // fileIO.fileIOImpl();

        fileIO.Task fileIOTask = new fileIO.Task();
        fileIOTask.task();

    }
}