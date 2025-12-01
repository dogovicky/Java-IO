import buffered.BufferingIO;
import buffered.Task;
import channels.FileChannelImpl;
import channels.NIOChannelsBuffers;
import fileIO.FileIOImpl;
import nio.NIO2;
import nonBlockingIO.NIOSelectors;
import nonBlockingIO.WebFluxSimulation;
import serialization.SerializationImpl;
import streams.StreamAPI;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

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
        //fileIOTask.task();

        // ============== NIO2 ===============
        NIO2 nio2 = new NIO2();
        // nio2.nio2Impl();


        // ============== NIO Channels and Buffers ================
        NIOChannelsBuffers bufferChannels = new NIOChannelsBuffers();
        // bufferChannels.nioChannelsAndBuffers();

        // File Channels
        FileChannelImpl fileChannel = new FileChannelImpl();
        // fileChannel.fileChannelImpl();

        // NIO Selectors (Non Blocking)
        NIOSelectors nioSelectors = new NIOSelectors();
        // nioSelectors.nioSelectorsImpl();

        WebFluxSimulation fluxSimulation = new WebFluxSimulation();
        // fluxSimulation.webFluxSimulation();

        // ============= Serialization ===============
        SerializationImpl serialization = new SerializationImpl();
        serialization.serializationImpl();
    }
}