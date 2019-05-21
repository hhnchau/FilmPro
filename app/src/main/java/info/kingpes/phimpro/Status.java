package info.kingpes.phimpro;

public class Status {
    private String name;
    private String status;

    public Status(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public Status() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
