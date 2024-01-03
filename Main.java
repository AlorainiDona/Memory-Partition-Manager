import java.io.*;
import java.util.*;

public class Main {
    static List<Partition> memory = new ArrayList<>();
    static int partitionCount;
    static int[] partitionSizes;
    static String allocationStrategy;

    public static void main(String[] args){
        initializeMemory();
        displayMenu();}

    public static void initializeMemory() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of partitions: ");
        partitionCount = scanner.nextInt();
        partitionSizes = new int[partitionCount];

        System.out.println("Enter the size of each partition in KB:");
        for (int i = 0; i < partitionCount; i++) {
            System.out.print("Partition " + (i + 1) + ": ");
            partitionSizes[i] = scanner.nextInt();
        }

        System.out.print("Enter the allocation strategy (F for First-fit, B for Best-fit, W for Worst-fit): ");
        allocationStrategy = scanner.next();

        int startAddress = 0;
        for (int i = 0; i < partitionCount; i++) {
            int size = partitionSizes[i];
            int endAddress = startAddress + size - 1;
            memory.add(new Partition(partitionSizes[i], startAddress, endAddress));
            startAddress += size;
        }
    }

    public static void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nMenu:");
            System.out.println("1. Allocate a block of memory");
            System.out.println("2. De-allocate a block of memory");
            System.out.println("3. Report detailed information about memory partitions");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    allocateMemory();
                    break;
                case 2:

                    System.out.println("Enter the process' ID");
                    String processID = scanner.next();
                    deallocateMemory(processID);
                    break;
                case 3:
                    displayMemoryStatus();
                    writeOutputToFile();
                    break;
                case 4:
                    System.out.println("Exiting the program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while (choice != 4);
    }

    public static void allocateMemory() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter process ID: ");
        String processID = scanner.next();
        System.out.print("Enter process size (KB): ");
        int processSize = scanner.nextInt();

        int index = -1;
        int maxFragmentation = -1;
        int minFragmentaion = -1;

        for (int i = 0; i < partitionCount; i++) {
            Partition partition = memory.get(i);
            if (partition.status.equals("free") && partition.size >= processSize) {
                if (allocationStrategy.equals("W") || allocationStrategy.equals("w")) {
                    int fragmentation = partition.size - processSize;
                    if (fragmentation > maxFragmentation) {
                        index = i;
                        maxFragmentation = fragmentation;
                    }
                } else if (allocationStrategy.equals("F")||allocationStrategy.equals("f")) {
                    index = i;
                    break;
                } else if (allocationStrategy.equals("B")||allocationStrategy.equals("b")) {
                    int fragmentation = partition.size - processSize;
                    if (index == -1) {
                        index = i;
                        minFragmentaion = fragmentation;
                    }
                    else if (fragmentation < minFragmentaion) {
                        index = i;
                        minFragmentaion = fragmentation;
                    }

                }
            }
        }

        if (index != -1) {
            Partition partition = memory.get(index);
            partition.status = "allocated";
            partition.processID = processID;
            partition.internalFragmentation = partition.size - processSize;
            System.out.println("Memory allocated successfully!");
        } else {
            System.out.println("Insufficient memory to allocate the process.");
        }
    }


    public static void displayMemoryStatus() {
        System.out.println("\nMemory Partition Information:");
        System.out.println("+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+");
        System.out.println("| Partition Size  | Partition Status| Process Number  | Fragmentation   | Starting        | Ending          |");
        System.out.println("|                 |                 |                 | Size            | Address         | Address         |");
        System.out.println("+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+");
        for (int i = 0; i < partitionCount; i++) {
            Partition partition = memory.get(i);

            System.out.printf("| %-15d | %-15s | %-15s | %-15s | %-15d | %-15d |%n", partition.size, partition.status, partition.processID, partition.internalFragmentation, partition.startAddress, partition.endAddress);
            System.out.println("+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+");

        }
        System.out.println("Memory State: ");
        System.out.print("[");
        for (int i = 0; i < partitionCount; i++) {
            Partition partition = memory.get(i);
            if (partition.processID.equals("Null")) {
                System.out.print("H");
            } else {
                System.out.print(partition.processID);
            }
            if (i < partitionCount - 1)
                System.out.print(" | ");}
        System.out.println("]");
    }

    public static void deallocateMemory(String name){
        for (int i = 0; i < partitionCount; i++) {
            Partition partition = memory.get(i);

            if(partition.processID.equals(name)){
                partition.status = "free";
                partition.processID = "Null";
                partition.internalFragmentation = -1;
                break;
            }
            else {
                System.out.println("Process is not found.");
            }
        }
    }

    public static void writeOutputToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("file.txt"))) {
            writer.write("\nMemory Partition Information:");
            writer.newLine();
            writer.write("+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+");
            writer.newLine();
            writer.write("| Partition Size  | Partition Status| Process Number  | Fragmentation   | Starting        | Ending          |");
            writer.newLine();
            writer.write("|                 |                 |                 | Size            | Address         | Address         |");
            writer.newLine();
            writer.write("+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+");
            writer.newLine();

            for (int i = 0; i < partitionCount; i++) {
                Partition partition = memory.get(i);

                writer.write(String.format("| %-15d | %-15s | %-15s | %-15s | %-15d | %-15d |", partition.size, partition.status, partition.processID, partition.internalFragmentation, partition.startAddress, partition.endAddress));
                writer.newLine();
                writer.write("+-----------------+-----------------+-----------------+-----------------+-----------------+-----------------+");
                writer.newLine();

            }
            writer.write("Memory State: ");
            writer.write("[");
            for (int i = 0; i<partitionCount;i++){
                Partition partition = memory.get(i);
                if (partition.processID.equals("Null")){
                    writer.write("H");
                } else {
                    writer.write(partition.processID);
                }
                if (i<partitionCount-1)
                    writer.write(" | ");}
            writer.write("]");


        } catch (IOException e) {
            System.out.println("An error occurred while writing the output to the file.");
            e.printStackTrace();
        }
    }


}
