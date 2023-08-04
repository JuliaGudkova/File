import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static String[] employee = "1,John,Smith,USA,25".split(",");
    private static String[] employee1 = "2,Inav,Petrov,RU,23".split(",");
    private static String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
    private static String fileName = "data.csv";
    private static List<Employee> list;
    private static String jsonCsv;
    private static List<Employee> list1;
    private static String jsonXml;

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException,
            TransformerException{
        writerCsv(employee,employee1);
        list = parseCSV(columnMapping, fileName);
        jsonCsv = listToJson(list);
        writeString("data.json");
        createXml();
        list1 = parseXML("data.xml");
        jsonXml = listToJson(list1);
        writeString("data2.json");


    }

//создаем файл csv из массива строк
    public static void writerCsv(String[] employee, String[]employee1){
        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            writer.writeNext(employee);
            writer.writeNext(employee1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //парсим файл csv и получаем список сотрудников
    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }
//преобразуем полученный список сотрудников в формат json
    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }
//записываем полученный json в файл
    public static void writeString(String fileName) {
        try (FileWriter fail = new FileWriter(fileName)) {
            fail.write(jsonCsv);
            fail.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //создаем файл xml
    private static void createXml() throws ParserConfigurationException,
            TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("root");
        document.appendChild(root);
        Element staff = document.createElement("staff");
        root.appendChild(staff);
        Element employee = document.createElement("employee");
        employee.setAttribute("id", "1");
        employee.setAttribute("firstName", "John");
        employee.setAttribute("lastName", "Smith");
        employee.setAttribute("country", "USA");
        employee.setAttribute("age", "25");
        employee.setAttribute("firstName", "Inav");
        employee.setAttribute("lastName", "Petrov");
        employee.setAttribute("country", "RU");
        employee.setAttribute("age", "23");
        staff.appendChild(employee);
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);

    }

    public static List<Employee> parseXML(String fileName) throws IOException, ParserConfigurationException, SAXException {
        List<Employee> staff = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        Node root = doc.getDocumentElement();//получили корневой узел
        NodeList nodeList = root.getChildNodes();//извлекаем список узлов
        //идем по списку узлов
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            //проверяем является ли полученная узел элементом
            if(node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                Employee employee3 = null;
                if (element == element.getElementsByTagName("employee")) {
                    employee3 = new Employee();
                    employee3.id = (Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()));
                    employee3.firstName = (element.getElementsByTagName("firstName").item(0).getTextContent());
                    employee3.lastName = (element.getElementsByTagName("lastName").item(0).getTextContent());
                    employee3.country = (element.getElementsByTagName("country").item(0).getTextContent());
                    employee3.age = (Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent()));
                }
                staff.add(employee3);
            }
        }
        return staff;
    }
}


