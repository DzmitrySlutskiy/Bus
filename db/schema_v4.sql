
-- Table: android_metadata
CREATE TABLE android_metadata ( 
    locale TEXT 
);


-- Table: News
CREATE TABLE News ( 
    _id      INTEGER PRIMARY KEY AUTOINCREMENT,
    NewsText TEXT 
);


-- Table: StopList
CREATE TABLE StopList ( 
    _id      INTEGER PRIMARY KEY AUTOINCREMENT,
    StopName TEXT 
);


-- Table: Routes
CREATE TABLE Routes ( 
    _id       INTEGER PRIMARY KEY AUTOINCREMENT,
    BusNumber TEXT,
    RouteName TEXT 
);


-- Table: RouteList
CREATE TABLE RouteList ( 
    _id       INTEGER PRIMARY KEY AUTOINCREMENT,
    RouteId   INTEGER,
    StopId    INTEGER,
    StopIndex INTEGER 
);


-- Table: TimeList
CREATE TABLE TimeList ( 
    _id         INTEGER PRIMARY KEY AUTOINCREMENT,
    RouteListId INTEGER,
    Hour        INTEGER,
    Minutes     TEXT,
    TypeId      TEXT 
);


-- Table: TypeList
CREATE TABLE TypeList ( 
    _id  INTEGER PRIMARY KEY AUTOINCREMENT,
    Type TEXT 
);


-- Index: idx_RouteList
CREATE INDEX idx_RouteList ON RouteList ( 
    RouteId ASC 
);


-- Index: idx_StopList
CREATE INDEX idx_StopList ON StopList ( 
    StopName ASC 
);


-- Index: idx_TimeList
CREATE INDEX idx_TimeList ON TimeList ( 
    RouteListId ASC 
);

