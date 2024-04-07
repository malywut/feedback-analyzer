package engineering.epic;

import java.util.ArrayList;
import java.util.List;

public class UserFeedback {
    private int birthYear;
    private String nationality;
    private String gender;
    private String feedback;
    private List<AtomicFeedback> atomicFeedbacks = new ArrayList<>();

    public UserFeedback(int age, String nationality, String gender, String feedback) {
        this.birthYear = java.time.Year.now().getValue() - age;
        this.nationality = nationality;
        this.feedback = feedback;
        this.gender = gender;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonFeedback{");
        sb.append("\nbirthYear=").append(birthYear);
        sb.append(", \nnationality='").append(nationality).append('\'');
        sb.append(", \ngender='").append(gender).append('\'');
        sb.append(", \nfeedback='").append(feedback).append('\'');
        if(!atomicFeedbacks.isEmpty()) {
            sb.append("\natomicFeedbacks=");
            for (AtomicFeedback atomicFeedback : atomicFeedbacks) {
                sb.append(atomicFeedback.prettyPrint());
            }
        }
        sb.append('}');
        return sb.toString();
    }

    public void setAtomicFeedbacks(List<AtomicFeedback> atomicFeedbacks) {
        this.atomicFeedbacks = atomicFeedbacks;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public List<AtomicFeedback> getAtomicFeedbacks() {
        return atomicFeedbacks;
    }

    public void addAtomicFeedback(AtomicFeedback atomicFeedback) {
        atomicFeedbacks.add(atomicFeedback);
    }
}