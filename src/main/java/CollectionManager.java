import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import messages.AnswerMsg;
import messages.Status;

import javax.management.modelmbean.XMLParseException;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

public class CollectionManager {

    private Queue<StudyGroup> groups;
    private final Date initDate;
    String savePath;

    public CollectionManager(String collectionPath) throws IOException {
        if (collectionPath == null)
            throw new FileNotFoundException("File path should be passed to program by using: command line argument");
        try {
            savePath = collectionPath;
            load(collectionPath);
        } catch (XMLParseException | XMLStreamException | IOException exception) {
            System.out.println("Error while parsing from file");
            System.exit(0);
        } catch (Exception e) {
            System.out.println("Something gone wrong with file");
            System.exit(0);
        }

        this.initDate = new Date();


    }

    /**
     * Loading collection from file
     */
    public void load(String collectionPath) throws IOException, XMLParseException, XMLStreamException {
        groups = new PriorityQueue<>();
        File file = new File(collectionPath);
        ObjectMapper xmlMapper = new XmlMapper();

        String xml = inputStreamToString(new FileInputStream(file));
        groups = xmlMapper.readValue(xml, new TypeReference<PriorityQueue<StudyGroup>>() {
        });
        if (!groups.isEmpty()) {
            System.out.println("Collection created");
        } else System.out.println("Empty collection");
    }

    /**
     * HelpMethod for  load
     * BufferReading
     */
    private String inputStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        String line = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((line = br.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append(line);
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Show Commands
     * Read help-file
     *
     * @param ans
     */
    public void help(AnswerMsg ans) {
        File helpFile = new File("C:\\Users\\1\\Desktop\\server\\src\\main\\java\\help.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(helpFile);
        } catch (Exception e) {
            e.printStackTrace();
            ans.AddErrorMsg("help.txt not found");
            ans.AddStatus(Status.ERROR);
        }
        try {
            while (scanner.hasNextLine()) ans.AddAnswer(scanner.nextLine());
        } catch (Exception e) {
            ans.AddStatus(Status.ERROR);
        }
    }

    /**
     * Info about collection
     */
    public void info(AnswerMsg ans) {
        try {
            ans.AddAnswer("" +
                    "Collection Type: " + groups.getClass().getSimpleName() +
                    "\nInitialization date : " + initDate +
                    "\nCollection size : " + groups.size());
        } catch (NullPointerException e) {
            ans.AddStatus(Status.ERROR);
            ans.AddErrorMsg("Empty collection");
        }
    }


    public void add(StudyGroup studyGroup, AnswerMsg ans) {
        studyGroup.toString();
        groups.add(studyGroup);
        ans.AddAnswer("Element was added");
    }

    /**
     * Show collection elements
     */
    public void show(AnswerMsg ans) {

        if (groups.size() != 0)
            for (StudyGroup group : groups) {
                ans.AddAnswer(group.toString() + "\n");
            }
        else ans.AddAnswer("Empty collection");

    }

    /**
     * Update element of collection by ID
     *
     * @param
     * @param id
     */
    public void update(Object updatedGroup, AnswerMsg ans, String id) {
        try {
            StudyGroup studyGroup = (StudyGroup) updatedGroup;
            Long thisId = Long.parseLong(id);
            if (groups.size() != 0) {
                for (StudyGroup group : groups) {
                    if (group.getId().equals(thisId)) {
                        group.setName(studyGroup.name);
                        group.getCoordinates().setX(studyGroup.getCoordinates().getX());
                        group.getCoordinates().setY(studyGroup.coordinates.getY());
                        group.setStudentsCount(studyGroup.getStudentsCount());
                        group.setFormOfEducation(studyGroup.getFormOfEducation());
                        group.setSemesterEnum(studyGroup.getSemesterEnum());
                        group.getGroupAdmin().setName(studyGroup.getGroupAdmin().getName());
                        group.getGroupAdmin().setBirthday(studyGroup.getGroupAdmin().getBirthday());
                        group.getGroupAdmin().setHeight(studyGroup.getGroupAdmin().getHeight());
                        group.getGroupAdmin().setWeight(studyGroup.getGroupAdmin().getWeight());
                        group.getGroupAdmin().setPassportID(studyGroup.getGroupAdmin().getPassportID());
                    }
                }
            } else ans.AddAnswer("Empty collection");
        } catch (Exception e) {
            ans.AddErrorMsg("Incorrect ID");
            ans.AddStatus(Status.ERROR);
        }
        ans.AddAnswer("Element successfully updated");
    }

    /**
     * Clear Collection
     *
     * @param ans
     */
    public void clear(AnswerMsg ans) {
        try {
            ans.AddAnswer("Clearing...");
            if (!groups.isEmpty()) {
                groups.clear();
                ans.AddAnswer("Successfully cleared");

            } else {
                ans.AddErrorMsg("Impossible to clear empty collection");
                ans.AddStatus(Status.ERROR);
            }
        } catch (Exception e) {
            ans.AddErrorMsg("ya ne ebu ch sluchilos");
            ans.AddStatus(Status.ERROR);
        }
    }

//    /**
//     * Сохранение коллекции в файл
//     *
//     * @param ans
//     */
//    public void save(AnswerMsg ans) {
//        XmlMapper xmlMapper = new XmlMapper();
//        File file = new File(savePath);
//        System.out.println("Saving....");
//
//        try {
//            xmlMapper.writeValue(file, groups);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Successfully saved");
//    }

    /**
     * Delete element of collection by id
     */
    public void remove_by_id(String id, AnswerMsg ans) {
        if (groups.isEmpty()) ans.AddAnswer("Empty collection");
        else
            try {
                Long removeID = Long.parseLong(id);
                ans.AddStatus(Status.ERROR);
                for (StudyGroup group : groups) {
                    if (group.getId().equals(removeID)) {
                        groups.remove(group);
                        ans.AddStatus(Status.FINE);
                    }
                }
                if (ans.getStatus().equals(Status.FINE)) {
                    ans.AddAnswer("Group successfully deleted");

                } else {
                    ans.AddStatus(Status.ERROR);
                    ans.AddErrorMsg("Group with " + removeID + " hasn't found");
                }
            } catch (Exception e) {
                ans.AddStatus(Status.ERROR);
                ans.AddErrorMsg("Incorrect ID");
            }
    }

    /**
     * Delete element of collection by FormOfEducation
     */
    public void remove_all_by_form_of_education(String form, AnswerMsg ans) {
        try {
            for (StudyGroup group : groups) {
                ans.AddStatus(Status.ERROR);
                if (group.getFormOfEducation().equals(FormOfEducation.valueOf(form))) {
                    groups.remove(group);
                    ans.AddAnswer("Group " + group.getName() + "  was deleted");
                    ans.AddStatus(Status.FINE);
                }
            }
            if (ans.getStatus().equals(Status.FINE)) {
                ans.AddAnswer("Groups with  " + form + " was deleted");
            } else {
                ans.AddErrorMsg("No group with this for of education");
                ans.AddStatus(Status.ERROR);
            }
        } catch (Exception e) {
            ans.AddErrorMsg("Incorrect form of education");
            ans.AddStatus(Status.ERROR);
        }
    }

    /**
     * Shows count of groups with entered semester
     */
    public void count_by_semester_enum(String semeter, AnswerMsg ans) {
        if (semeter.equals("THIRD") || semeter.equals("FIFTH") || semeter.equals("SIXTH")) {

            try {
                int counter = 0;

                for (StudyGroup group : groups) {
                    if (group.getSemesterEnum().equals(Semester.valueOf(semeter))) {
                        counter++;
                    }
                }
                ans.AddAnswer("Founded " + counter + " groups");
            } catch (Exception e) {
                ans.AddErrorMsg("ono slomalos");
            }

        } else{
            ans.AddStatus(Status.ERROR);
            ans.AddErrorMsg("Entered incorrect semester");
        }

    }

    /**
     * Add new element if, it's count of students less than minimum
     */
    public void add_if_min(StudyGroup studyGroup, AnswerMsg answerMsg) {
        try {
            if (studyGroup.getStudentsCount() < groups.poll().getStudentsCount()) {
                groups.add(studyGroup);
                answerMsg.AddAnswer("Group successfully added");
            } else {
                answerMsg.AddAnswer("Founded less count of students");
            }
        } catch (Exception e) {
            answerMsg.AddStatus(Status.ERROR);
            answerMsg.AddErrorMsg("GOD BLESS");
        }
    }

    /**
     * Shows first element add delete it
     *
     * @param ans
     */
    public void remove_head(AnswerMsg ans) {
        if (groups.isEmpty()) {
            ans.AddAnswer("Collection is empty");
        } else {
            ans.AddAnswer(groups.poll().toString());
        }
    }

    /**
     * Show unique semesterEnum
     *
     * @param ans
     */
    public void print_unique_semester_enum(AnswerMsg ans) {
        int[] hasEnum = {0, 0, 0};
        boolean hasUnique = false;
        if (!groups.isEmpty()) {
            ans.AddAnswer("Searching unique  semesterEnum....");
            for (StudyGroup group : groups) {
                if (group.getSemesterEnum().equals(Semester.THIRD)) {
                    hasEnum[0] = hasEnum[0] + 1;
                }
                if (group.getSemesterEnum().equals(Semester.FIFTH)) {
                    hasEnum[1] = hasEnum[0] + 1;
                }
                if (group.getSemesterEnum().equals(Semester.SIXTH)) {
                    hasEnum[2] = hasEnum[0] + 1;
                }

            }
            if (hasEnum[0] == 1) {
                ans.AddAnswer("THIRD");
                hasUnique = true;
            }
            if (hasEnum[1] == 1) {
                ans.AddAnswer("FIFTH");
                hasUnique = true;
            }
            if (hasEnum[2] == 1) {
                ans.AddAnswer("SIXTH");
                hasUnique = true;
            }
            if (!hasUnique) ans.AddAnswer("Unique semesterEnum wasn't founded");
        } else ans.AddAnswer("Empty collection" +
                "");
    }
}


