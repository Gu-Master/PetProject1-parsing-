package pogoda;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static Pattern pattern = Pattern.compile("\\d{2}\\.\\d{2}");

    private static void printForValues(Elements values, int index) {
        for (int i = 0, j = 0; i < 4; i++, j = 0) {
            Element valueLine = values.get(index + i);
            for (Element el : valueLine.select("td")) {
                if (j == 0) {
                    System.out.printf("%-7s", el.text());
                } else if (j == 1) {
                    System.out.printf("%-17s", el.text().substring(0, 15));
                } else {
                    System.out.printf("%-15s", el.text());
                }
                j++;
            }
            System.out.println();
        }
    }

    private static void printForValuesForFile(Elements values, int index, BufferedWriter bufferedWriter, FileWriter fileWriter) throws IOException {
        for (int i = 0, j = 0; i < 4; i++, j = 0) {
            Element valueLine = values.get(index + i);
            for (Element el : valueLine.select("td")) {
                if (j == 0) {
                    bufferedWriter.write(String.format("%-7s", el.text()));
                } else if (j == 1) {
                    bufferedWriter.write(String.format("%-17s", el.text().substring(0, 15)));
                } else {
                    bufferedWriter.write(String.format("%-15s", el.text()));
                }
                j++;
            }
            bufferedWriter.newLine();
        }
    }

    private static String getDateString(String str) throws Exception {
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new Exception("Can't find date");
    }

    private static Document getPage() throws IOException {
        String url = "https://www.pogoda.spb.ru/";
        String url2 = "http://www.pogoda.spb.ru/10days/";
        Document page = Jsoup.parse(new URL(url2), 3000);
        return page;
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        Document page = getPage();
        Element tableWth = page.select("table[class=wt]").first();
        Elements names = tableWth.select("tr[class=wth]");
        Elements values = tableWth.select("tr[valign=top]");
        int ind = 0;
        System.out.print("Вас приветствует парсер погоды Санкт Питербурга =)\n" +
                "Если хотите, чтобы данные записались в файл, то нажмите 1 или нажмите просто" +
                " Enter и вывод поступит на экран\nВаш выбор : ");
        switch (scanner.nextLine()) {
            case "1":
                String outputFileName = "output.txt";
                String currentDir = System.getProperty("user.dir");

                // Создание объекта File для файла вывода в текущей директории
                File outputFile = new File(currentDir, outputFileName);
                // Создание объекта FileWriter для записи в файл
                FileWriter fileWriter = new FileWriter(outputFile);

                // Создание объекта BufferedWriter для буферизации записи
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                for (Element name : names) {
                    String dateString = name.select("th[id=dt]").text();
                    String date = getDateString(dateString);
                    bufferedWriter.write(date + "   Явления         Температура    Давление     Влажность       Ветер");
                    bufferedWriter.newLine();
                    printForValuesForFile(values, ind, bufferedWriter, fileWriter);
                    bufferedWriter.newLine();
                    ind += 4;
                }
                bufferedWriter.close();
                System.out.println("Файл создан и имеет данные о погоде в Питере. Сам файл находиться в " + currentDir);
                break;
            default:
                for (Element name : names) {
                    String dateString = name.select("th[id=dt]").text();
                    String date = getDateString(dateString);
                    System.out.println(date + "   Явления         Температура    Давление     Влажность       Ветер");
                    printForValues(values, ind);
                    ind += 4;
                }
                ;
        }

    }
}
