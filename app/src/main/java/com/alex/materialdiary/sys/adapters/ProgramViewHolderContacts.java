package com.alex.materialdiary.sys.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alex.materialdiary.R;

public class ProgramViewHolderContacts {

    TextView Names;
    TextView Unread;
    ImageView img;
    // Get the handles by calling findViewById() on View object inside the constructor
    ProgramViewHolderContacts(View v)
    {
        Names = v.findViewById(R.id.contact_name);
        Unread = v.findViewById(R.id.unreadedM);
        img = v.findViewById(R.id.user_icon);
    }
}