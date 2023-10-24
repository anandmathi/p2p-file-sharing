import java.io.*;

public class Common {

    private int numOfPrefNeigh;
    private int unchoIntv;
    private int optChokIntv;
    private String name;
    private int fileSize;
    private int pieceSize;
    // Do we need to add number of pieces?

    public Common() {
        readCommon("config/Common.cfg"); // Put the file name "Common.cfg" with the path
    }

    // get the each setValue of "Common.cfg" file
    public int getNumOfPrefNeigh() {
        return numOfPrefNeigh;
    }

    public int getUnchokIntvl() {
        return unchoIntv;
    }

    public int getOptChokIntv() {
        return optChokIntv;
    }

    public String getFileName() {
        return name;
    }

    public int getFileSize() {
        return fileSize;
    }

    public int getPieceSize() {
        return pieceSize;
    }

    // Set the each setValue of "Common.cfg" file
    public void setNumOfPrefNeigh(int numOfPrefNeigh) {
        this.numOfPrefNeigh = numOfPrefNeigh;
    }

    public void setUnchokIntv(int unchoIntv) {
        this.unchoIntv = unchoIntv;
    }

    public void setOptChokIntv(int optChokIntv) {
        this.optChokIntv = optChokIntv;
    }

    public void setFileName(String name) {
        this.name = name;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public void setPieceSize(int pieceSize) {
        this.pieceSize = pieceSize;
    }

    /*
     * Read the given file (Overlapping features with 'parserCommon()'function in
     * the ConfigParser.java
     * Need to integrate and coordinate after consultation)
     */
    private void readCommon(String givenFile) {
        BufferedReader reader;
        try {

            reader = new BufferedReader(new FileReader(givenFile));
            String row = reader.readLine();
            String[] contents = row.split(" ");
            String title = contents[0];
            String setValue = contents[1];

            /*
             * Get the each row's title and set the value
             * Change the value of each title stored in the string type to Int type
             * to use Integer.parseInt()
             */
            switch(title) {
                case "NumberOfPreferredNeighbors":
                    setNumOfPrefNeigh(Integer.parseInt(setValue));
                    break;
                
                case "UnchokingInterval":
                    setUnchokIntv(Integer.parseInt(setValue));
                    break;

                case "OptimisticUnchokingInterval":
                    setOptChokIntv(Integer.parseInt(setValue));
                    break;

                case "FileName":
                    setFileName(setValue);
                    break;

                case "FileSize":
                    setFileSize(Integer.parseInt(setValue));
                    break;

                case "PieceSize":
                    setPieceSize(Integer.parseInt(setValue));
                    break;

                default:
                    break;
            }

            /*
             * While the file's row is empty
             * Almost similar to the codes written above
             * because of the while loop
             * requires optimization by combining two codes
             */
            while (row != null) {
                row = reader.readLine();
                contents = row.split(" ");
                title = contents[0];
                setValue = contents[1];

                switch(title) {
                    case "NumberOfPreferredNeighbors":
                        setNumOfPrefNeigh(Integer.parseInt(setValue));
                        break;
                
                    case "UnchokingInterval":
                        setUnchokIntv(Integer.parseInt(setValue));
                        break;

                    case "OptimisticUnchokingInterval":
                        setOptChokIntv(Integer.parseInt(setValue));
                        break;

                    case "FileName":
                        setFileName(setValue);
                        break;

                    case "FileSize":
                        setFileSize(Integer.parseInt(setValue));
                        break;

                    case "PieceSize":
                        setPieceSize(Integer.parseInt(setValue));
                        break;
                    
                    default:
                        break;
                }
            }
        }

        catch (IOException e) {
            // print the error message
        }
    }
}