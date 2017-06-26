package com.lwkandroid.indexbardemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 联系人数据实体类
 */

public class User implements Parcelable
{
    private String name;

    private String firstChar;

    public User(String name, String firstChar)
    {
        this.name = name;
        this.firstChar = firstChar;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFirstChar()
    {
        return firstChar;
    }

    public void setFirstChar(String firstChar)
    {
        this.firstChar = firstChar;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "name='" + name + '\'' +
                ", firstChar='" + firstChar + '\'' +
                '}';
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.name);
        dest.writeString(this.firstChar);
    }

    protected User(Parcel in)
    {
        this.name = in.readString();
        this.firstChar = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>()
    {
        @Override
        public User createFromParcel(Parcel source)
        {
            return new User(source);
        }

        @Override
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };
}
