package lk.ac.mrt.uom.uomonline.model;

import java.io.Serializable;
import java.io.StringReader;

/**
 * Created by sineth on 8/11/17.
 */

public class ArticleMinified implements Serializable{
    private String title;
    private String imageURL;
    private String id;

    public ArticleMinified() {
    }

    public ArticleMinified(String title, String imageURL,String id) {
        this.title = title;
        this.imageURL = imageURL;
        this.id = id;
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
}
