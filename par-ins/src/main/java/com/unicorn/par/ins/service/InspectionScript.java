package com.unicorn.par.ins.service;

import com.unicorn.par.ins.model.AutoInspection;

public interface InspectionScript {

    AutoInspection doInspection() throws Exception;

    String getSystemName();
}
