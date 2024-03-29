package com.example.cookbook.repository;

import android.content.Context;

import com.example.cookbook.AppDatabase;
import com.example.cookbook.dao.LabelsDao;
import com.example.cookbook.entity.Label;

import java.util.List;

public class LabelRepository {
    private final LabelsDao labelsDao;

    public LabelRepository(Context context) {
        labelsDao = AppDatabase.getInstance(context).labelsDao();
    }

    public List<Label> getAllLabels() {
        return labelsDao.getAllLabels();
    }
}
