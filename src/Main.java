import buffered.BufferingIO;
import buffered.Task;
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
        bufferedTask.task();

    }
}