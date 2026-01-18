import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// ==================================================
// 1. DOCUMENT EDITOR
// ==================================================

abstract class Document {
    protected String content;

    public Document(String content) {
        this.content = content;
    }

    public abstract String display();
    public abstract void save(String filename) throws IOException;
}

class PDFDocument extends Document {

    public PDFDocument(String content) {
        super(content);
    }

    @Override
    public String display() {
        return "[PDF]\n" + content;
    }

    @Override
    public void save(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("%PDF-1.4\n");
        writer.write(content);
        writer.close();
    }
}

class WordDocument extends Document {

    public WordDocument(String content) {
        super(content);
    }

    @Override
    public String display() {
        return "[WORD]\n" + content;
    }

    @Override
    public void save(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write("DOCX FORMAT\n");
        writer.write(content);
        writer.close();
    }
}

class HTMLDocument extends Document {

    public HTMLDocument(String content) {
        super(content);
    }

    @Override
    public String display() {
        return "<html><body>" + content + "</body></html>";
    }

    @Override
    public void save(String filename) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(display());
        writer.close();
    }
}

class DocumentFactory {
    public static Document createDocument(String format, String content) {
        switch (format.toLowerCase()) {
            case "pdf":
                return new PDFDocument(content);
            case "word":
                return new WordDocument(content);
            case "html":
                return new HTMLDocument(content);
            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
    }
}

// ==================================================
// 2. CAR BUILDER
// ==================================================

class Car {
    String engine;
    String transmission;
    List<String> interior = new ArrayList<>();
    List<String> exterior = new ArrayList<>();
    List<String> safety = new ArrayList<>();

    boolean isValid() {
        return engine != null && transmission != null;
    }

    Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Engine", engine);
        map.put("Transmission", transmission);
        map.put("Interior", interior);
        map.put("Exterior", exterior);
        map.put("Safety", safety);
        return map;
    }

    @Override
    public String toString() {
        return "Engine: " + engine + "\n" +
                "Transmission: " + transmission + "\n" +
                "Interior: " + interior + "\n" +
                "Exterior: " + exterior + "\n" +
                "Safety: " + safety;
    }
}

class CarBuilder {
    private Car car;

    public CarBuilder() {
        reset();
    }

    private void reset() {
        car = new Car();
    }

    public CarBuilder setEngine(String engine) {
        car.engine = engine;
        return this;
    }

    public CarBuilder setTransmission(String transmission) {
        if (!transmission.equals("manual") && !transmission.equals("automatic")) {
            throw new IllegalArgumentException("Transmission must be manual or automatic");
        }
        car.transmission = transmission;
        return this;
    }

    public CarBuilder addInterior(String feature) {
        car.interior.add(feature);
        return this;
    }

    public CarBuilder addExterior(String feature) {
        car.exterior.add(feature);
        return this;
    }

    public CarBuilder addSafety(String feature) {
        car.safety.add(feature);
        return this;
    }

    public Car build() {
        if (!car.isValid()) {
            throw new IllegalStateException("Car must have engine and transmission");
        }
        Car finishedCar = car;
        reset();
        return finishedCar;
    }
}

// ==================================================
// 3. CAR DOCUMENT GENERATOR
// ==================================================

class CarDocumentGenerator {

    public static String generateContent(Car car, String format) {
        Map<String, Object> data = car.toMap();

        if (format.equalsIgnoreCase("html")) {
            StringBuilder html = new StringBuilder();
            html.append("<h1>Car Configuration Report</h1><ul>");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                html.append("<li><b>")
                        .append(entry.getKey())
                        .append(":</b> ")
                        .append(entry.getValue())
                        .append("</li>");
            }
            html.append("</ul>");
            return html.toString();
        } else {
            StringBuilder text = new StringBuilder("Car Configuration Report\n");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                text.append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }
            return text.toString();
        }
    }

    public static Document createCarDocument(Car car, String format, String filename)
            throws IOException {

        String content = generateContent(car, format);
        Document doc = DocumentFactory.createDocument(format, content);
        if (filename != null) {
            doc.save(filename);
        }
        return doc;
    }
}

// ==================================================
// 4. MAIN
// ==================================================

public class Main {

    public static void main(String[] args) throws IOException {

        CarBuilder builder = new CarBuilder();

        Car car = builder
                .setEngine("V6")
                .setTransmission("automatic")
                .addInterior("Leather Seats")
                .addInterior("GPS Navigation")
                .addExterior("Blue Color")
                .addExterior("Alloy Wheels")
                .addSafety("ABS")
                .addSafety("Rear Camera")
                .build();

        System.out.println("Car Built Successfully:\n");
        System.out.println(car);

        String fileName = "car_report.html";
        Document document = CarDocumentGenerator.createCarDocument(car, "html", fileName);

        System.out.println("\nDocument Preview:\n");
        System.out.println(document.display());

        System.out.println("\nHTML file saved as: " + fileName);
    }
}
