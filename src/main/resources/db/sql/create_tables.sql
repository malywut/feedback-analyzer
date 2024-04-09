CREATE TABLE IF NOT EXISTS UserFeedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    birthYear INTEGER,
    nationality TEXT,
    gender TEXT,
    feedback TEXT
);

CREATE TABLE IF NOT EXISTS AtomicFeedback (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    userFeedbackId INTEGER,
    category TEXT,
    urgency INTEGER,
    severity INTEGER,
    impact INTEGER,
    feedback TEXT,
    FOREIGN KEY (userFeedbackId) REFERENCES UserFeedback(id)
);

CREATE TABLE IF NOT EXISTS Tag (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS AtomicFeedback_Tag (
    atomicFeedbackId INTEGER,
    tagId INTEGER,
    PRIMARY KEY (atomicFeedbackId, tagId),
    FOREIGN KEY (atomicFeedbackId) REFERENCES AtomicFeedback(id),
    FOREIGN KEY (tagId) REFERENCES Tag(id)
);