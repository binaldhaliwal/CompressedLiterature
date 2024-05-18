import java.io.*;
import java.util.Map;
/*
 * The main class for Compressed Literature.
 *
 * @author Binal Dhaliwal and Anagha Krishna
 */
public class Main {
    public static void main(String[] args) throws IOException {
        long startTime, endTime, totalTime;

        if (args.length == 0) {
            System.out.println("Provide the input file.");
            return;
        }
        File input = new File(args[0]);
        File file = new File("codes.txt");
        file.createNewFile();
        File compressedFile = new File("compressed.bin");
        compressedFile.createNewFile();
        StringBuilder string = new StringBuilder();
        int c;

        try {
            FileReader fileRead = new FileReader(input);
            BufferedReader buffer = new BufferedReader(fileRead);

            while((c = buffer.read()) != -1) {
                char character = (char) c;
                string.append(character);
            }
            buffer.close();
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }

        startTime = System.currentTimeMillis();
        CodingTree codingTree = new CodingTree(string.toString());
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;

        try (FileOutputStream outputStream = new FileOutputStream(compressedFile)) {
            byte[] data = new byte[codingTree.bits.size()];
            for (int i = 0; i < data.length; i++) {
                data[i] = codingTree.bits.get(i);
            }
            outputStream.write(data);
        }
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("{\n");
            int count = 0;
            for (Map.Entry<Character, String> entry : codingTree.codes.entrySet()) {
                fileWriter.write(String.format("%s=%s", entry.getKey(), entry.getValue()));
                count++;
                if (count < codingTree.codes.size()) {
                    fileWriter.write(", ");
                }
                if (count % 8 == 0) {
                    fileWriter.write("\n");
                }
            }
            fileWriter.write("\n}");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Original file size: %.3f kibibytes\n", input.length() / 1024.0);
        System.out.printf("Compressed file size: %.3f kibibytes\n", compressedFile.length() / 1024.0);
        System.out.printf("Compression ratio: %d%%\n", (int)(((double) compressedFile.length() / input.length()) * 100.0));
        System.out.println("Compression time: " + (double) totalTime + " milliseconds");

        double compression = ((double) compressedFile.length()) / input.length();
        double decompressedSize = compressedFile.length() / 1024.0 / compression;

        long decompressionStartTime = System.currentTimeMillis();

        String decoded = codingTree.decode(codingTree.bits, codingTree.codes, string.length());

        File decodedFile = new File("decodedtxt");
        try (FileWriter decodedWriter = new FileWriter(decodedFile)) {
            decodedWriter.write(decoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long decompressionEndTime = System.currentTimeMillis();
        long decompressionTotalTime = decompressionEndTime - decompressionStartTime;

        System.out.printf("Decompressed file size: %.3f kibibytes\n", decompressedSize);
        System.out.println("Decompressed time: " + (double) decompressionTotalTime + " milliseconds");
        System.out.print("Decompressed == original ?");
        if (decompressedSize == input.length() / 1024.0) {
            System.out.println("  true\n");
        }
        Main main = new Main();
        main.testCodingTree();
    }


    void testCodingTree(){
        String message = "This is a test message for CodingTree";
        CodingTree codingTree = new CodingTree(message);
        System.out.println("Codes:");
        for (Map.Entry<Character, String> entry : codingTree.codes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("Bits:");
        for (byte bit : codingTree.bits) {
            System.out.print(bit + " ");
        }
        System.out.println();
    }

}