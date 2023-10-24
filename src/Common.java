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
            if (title.equals("NumberOfPreferredNeighbors")) {
                setNumOfPrefNeigh(Integer.parseInt(setValue));
            }

            else if (title.equals("UnchokingInterval")) {
                setUnchokIntv(Integer.parseInt(setValue));
            }

            else if (title.equals("OptimisticUnchokingInterval")) {
                setOptChokIntv(Integer.parseInt(setValue));

            }

            else if (title.equals("FileName")) {
                setFileName(setValue);
            }

            else if (title.equals("FileSize")) {
                setFileSize(Integer.parseInt(setValue));
            }

            else if (title.equals("PieceSize")) {
                setPieceSize(Integer.parseInt(setValue));
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

                if (title.equals("NumberOfPreferredNeighbors")) {
                    setNumOfPrefNeigh(Integer.parseInt(setValue));
                }

                else if (title.equals("UnchokingInterval")) {
                    setUnchokIntv(Integer.parseInt(setValue));
                }

                else if (title.equals("OptimisticUnchokingInterval")) {
                    setOptChokIntv(Integer.parseInt(setValue));
                }

                else if (title.equals("FileName")) {
                    setFileName(setValue);
                }

                else if (title.equals("FileSize")) {
                    setFileSize(Integer.parseInt(setValue));
                }

                else if (title.equals("PieceSize")) {
                    setPieceSize(Integer.parseInt(setValue));
                }
            }
        }

        catch (IOException e) {
            // print the error message
        }
    }
}