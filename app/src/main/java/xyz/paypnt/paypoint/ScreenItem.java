package xyz.paypnt.paypoint;

import android.os.Parcel;
import android.os.Parcelable;

public class ScreenItem implements Parcelable {
    String Title,Description;
    int ScreenImg;

    public ScreenItem(String title, String description, int screenImg) {
        Title = title;
        Description = description;
        ScreenImg = screenImg;
    }

    protected ScreenItem(Parcel in) {
        Title = in.readString();
        Description = in.readString();
        ScreenImg = in.readInt();
    }

    public static final Creator<ScreenItem> CREATOR = new Creator<ScreenItem>() {
        @Override
        public ScreenItem createFromParcel(Parcel in) {
            return new ScreenItem(in);
        }

        @Override
        public ScreenItem[] newArray(int size) {
            return new ScreenItem[size];
        }
    };

    public void setTitle(String title) {
        Title = title;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setScreenImg(int screenImg) {
        ScreenImg = screenImg;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescription() {
        return Description;
    }

    public int getScreenImg() {
        return ScreenImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Title);
        dest.writeString(Description);
        dest.writeInt(ScreenImg);
    }
}
