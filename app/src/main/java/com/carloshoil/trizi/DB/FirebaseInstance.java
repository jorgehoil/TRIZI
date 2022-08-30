package com.carloshoil.trizi.DB;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInstance {
    public static FirebaseDatabase firebaseDatabase;

    public static FirebaseDatabase getInstance()
    {
        if(firebaseDatabase==null)
        {
            firebaseDatabase=FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(false);
        }
        return firebaseDatabase;

    }
}
