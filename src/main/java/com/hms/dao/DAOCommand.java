package com.hms.dao;

import java.io.Serializable;

public interface DAOCommand extends Serializable {
    Object execute(DAOProvider provider);
}

