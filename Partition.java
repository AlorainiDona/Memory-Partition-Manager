public class Partition {
    String status;
    int size;
    int startAddress;
    int endAddress;
    String processID;
    int internalFragmentation;

    public Partition(int size, int startAddress, int endAddress) {
        this.status = "free";
        this.size = size;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.processID = "Null";
        this.internalFragmentation = -1;
    }
}
