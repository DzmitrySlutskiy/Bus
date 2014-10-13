
-- Table: android_metadata
CREATE TABLE android_metadata ( 
    locale TEXT 
);


-- Table: StopList
CREATE TABLE StopList ( 
    _id      INTEGER PRIMARY KEY AUTOINCREMENT,
    StopName TEXT 
);


-- Table: BusList
CREATE TABLE BusList ( 
    _id       INTEGER PRIMARY KEY AUTOINCREMENT,
    BusNumber TEXT 
);


-- Table: News
CREATE TABLE News ( 
    _id      INTEGER PRIMARY KEY AUTOINCREMENT,
    NewsText TEXT 
);


-- Table: Routes
CREATE TABLE Routes ( 
    _id         INTEGER PRIMARY KEY AUTOINCREMENT,
    BusId       INTEGER,
    BeginStopId INTEGER,
    EndStopId   INTEGER 
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
    DayTypeId   INTEGER 
);


-- Table: TypeList
CREATE TABLE TypeList ( 
    _id  INTEGER PRIMARY KEY AUTOINCREMENT,
    Type TEXT 
);


-- Index: idx_Routes
CREATE INDEX idx_Routes ON Routes ( 
    BusId ASC 
);


-- Index: idxRouteListId
CREATE INDEX idxRouteListId ON TimeList ( 
    RouteListId ASC,
    Hour        ASC 
);

