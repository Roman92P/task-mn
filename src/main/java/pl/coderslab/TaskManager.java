package pl.coderslab;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.GenericValidator;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLOutput;
import java.util.*;
public class TaskManager {
    public static void main(String[] args) {
//powitanie użytkownika aplikacji + nazwa pliku, gdzie są przechowywane zadania;
        System.out.println("Welcome to task manager app [version Warsztaty Coderslab]");
        Scanner scanner = new Scanner(System.in);
        String nazwaPliku = "Tasks.csv";
        //sprawdzamy czy plik istnieje, jeśli nie tworzymy poprzez metodę fileExistOrNo;
        fileExistOrNo(nazwaPliku);
        if (Files.exists(Paths.get(nazwaPliku))) {
            while (true) {
//metoda wyświetljąca menu. Jeśli użytkownik wpisze niepoprawne val., zadziała switch default;
                menuBar();
                String choosenButton = scanner.nextLine();
                switch (choosenButton) {
                    case "add":
                        addTask(scanner, nazwaPliku);
                        break;
                    case "remove":
                        RemoveTask(scanner, nazwaPliku);
                        break;
                    case "list":
                        listTask(scanner, nazwaPliku);
                        break;
                    case "exit":
                        scanTillquit(choosenButton);
                        break;
                    default:
                        System.out.println(ConsoleColors.RED + "Please select a correct option.");
                }
                if ("exit".equals(choosenButton)) {
                    System.out.print(ConsoleColors.RED_BOLD + "bye bye");
                    break;
                }
                System.out.println(choosenButton);
            }
        }
        scanner.close();
    }
    private static void RemoveTask(Scanner scanner, String nazwaPliku) {
        System.out.println("Enter line number");
        int toDel = checkEnterNumLine(scanner, nazwaPliku);
        List<String> currentList = new ArrayList<>();
        Path path = Paths.get(nazwaPliku);
        try {
            for ( String lines : Files.readAllLines(path) ) {
                currentList.add(lines);
            }
            currentList.remove(toDel);
            List<String> tempList = new ArrayList<>();
            String smth = "";
            for ( int i = 0; i < currentList.size(); i++ ) {
                smth = currentList.get(i);
                char c = smth.charAt(0);
                int control = i + '0';
                if (c != control) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(i);
                    stringBuilder.append(StringUtils.substring(smth, 1));
                    smth = stringBuilder.toString();
                }
                tempList.add(smth);
            }
            for ( String lines : tempList ) {
                System.out.println(lines);
            }
            Files.write(path, tempList, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static int checkEnterNumLine(Scanner scanner, String nazwaPliku) {
        String userWrite = "";
        int max = numberOfLine(nazwaPliku);
        int result;
        while (true) {
            userWrite = scanner.nextLine();
            if (NumberUtils.isParsable(userWrite) && Integer.parseInt(userWrite) < max) {
                break;
            } else {
                System.out.println("Enter other number");
            }
        }
        result = Integer.parseInt(userWrite);
        return result;
    }
    private static void addTask(Scanner scanner, String nazwaPliku) {
        List<String> lines = null;
        try {
            String[] nowaLinia = new String[3];
            System.out.println("Write the description of a new task");
            String describe = "";
            while (true) {
                describe = scanner.nextLine();
                if (!StringUtils.isEmpty(describe)) {
                    break;
                } else {
                    System.out.print("This field cannot be empty. Write something");
                }
            }
            nowaLinia[0] = numberOfLine(nazwaPliku) + " : " + describe;
            System.out.println("Write due date");
            String dt = "";
            while (true) {
                dt = scanner.nextLine();
                if (GenericValidator.isDate(dt, "yyyy-MM-dd", true)) {
                    break;
                } else {
                    System.out.print("Please enter Date in this format YYYY-MM-DD");
                }
            }
            nowaLinia[1] = dt;
            System.out.print("Is it important or no [put true or false]");
            String importance = "";
            while (scanner.hasNextLine()) {
                importance = scanner.nextLine();
                if (importance.contains("true") || importance.contains("false")) {
                    nowaLinia[2] = importance;
                    break;
                } else {
                    System.out.print("You should enter true or false, nothing else");
                }
            }
            String newLine = Arrays.toString(nowaLinia);
            String out = newLine.substring(newLine.indexOf("[") + 1, newLine.indexOf("]"));
            Path path = Paths.get(nazwaPliku);
            lines = new ArrayList<>();
            for ( String line : Files.readAllLines(path) ) {
                lines.add(line);
            }
            lines.add(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path path = Paths.get(nazwaPliku);
        try {
            Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        listTask(scanner, nazwaPliku);
    }
    private static void fileExistOrNo(String nazwaPliku) {
        if (!Files.exists(Paths.get(nazwaPliku))) {
            try {
                Files.createFile(Paths.get(nazwaPliku));
                System.out.println(ConsoleColors.RED_BOLD + "File don't exist so you can create him by choosind add");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void listTask(Scanner scan, String nazwaPliku) {
        int rowc = 0;
        String[][] taskTab = new String[numberOfLine(nazwaPliku)][3];
        try {
            scan = new Scanner(new BufferedReader(new FileReader(nazwaPliku)));
            String inputLine = "";
            while (scan.hasNextLine()) {
                inputLine = scan.nextLine();
                String[] rowArr = inputLine.split(",");
                for ( int i = 0; i < rowArr.length; i++ ) {
                    taskTab[rowc][i] = rowArr[i];
                }
                rowc++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        for ( int i = 0; i < taskTab.length; i++ ) {
            for ( int j = 0; j < taskTab[i].length; j++ ) {
                System.out.print(taskTab[i][j]);
            }
            System.out.println("\n");
        }
    }
    private static int numberOfLine(String fName) {
        Path path1 = Paths.get(fName);
        int count = 0;
        try {
            for ( String zm : Files.readAllLines(path1) ) {
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }
    private static void scanTillquit(String input) {
        while (true) {
            if ("exit".equals(input))
                break;
        }
    }
    private static void menuBar() {
        String[] options = {(ConsoleColors.BLUE_BOLD + "Please select option") + ConsoleColors.BLACK, "add", "remove", "list", "exit"};
        for ( String op : options ) {
            System.out.print(op + "\n");
        }
    }
}