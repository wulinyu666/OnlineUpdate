package com.zengweicong.uploadapp;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

public class tools {
    public boolean fileIsExists(String strFile) {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
}
