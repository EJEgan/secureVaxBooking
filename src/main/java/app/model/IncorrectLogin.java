package app.model;

public class IncorrectLogin {
    private String ipAddress;
    private int numAttempts;

    public IncorrectLogin(String ipAddress) {
        // Whenever a new incorrect login is attempted record the ipAddress and instantiate the counter of incorrect
        // attempts at 1
        this.ipAddress = ipAddress;
        this.numAttempts = 1;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getNumAttempts() {
        return this.numAttempts;
    }

    public void setNumAttempts(int numAttempts) {
        this.numAttempts = numAttempts;
    }
}
