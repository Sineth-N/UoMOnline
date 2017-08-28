package lk.ac.mrt.uom.uomonline.model;

import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by sineth on 8/11/17.
 */

public class Article implements Serializable{
    private String title;
    private String imageURL;
    private String id;
    private String tagLine;
    private String story;

    public Article() {
    }

    public Article(String title, String imageURL, String id, String tagLine, String story) {
        this.title = title;
        this.imageURL = imageURL;
        this.id = id;
        this.tagLine = tagLine;
        this.story = story;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTagLine() {
        return tagLine;
    }

    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

}