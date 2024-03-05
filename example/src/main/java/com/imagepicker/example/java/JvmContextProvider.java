package com.imagepicker.example.java;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kotlin.jvm.functions.Function0;

public class JvmContextProvider implements Function0<Context> {

    @Nullable
    private Activity hostAct;
    @Nullable
    private Fragment hostFrag;

    public JvmContextProvider(@NonNull Activity host) {
        this.hostAct = host;
    }

    public JvmContextProvider(@NonNull Fragment host) {
        this.hostFrag = host;
    }

    @Override
    public Context invoke() {
        if (hostAct != null) {
            return hostAct;
        } else if (hostFrag != null) {
            return hostFrag.requireContext();
        } else {
            throw new IllegalArgumentException();
        }
    }
}