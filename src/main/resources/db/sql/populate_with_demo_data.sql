-- Enabling foreign key support
PRAGMA foreign_keys = ON;

-- Inserting sample data into UserFeedback
INSERT INTO UserFeedback (birthYear, nationality, gender, feedback) VALUES
(1984, 'American', 'Male', 'Great session, but too long and the room was too cold.'),
(1990, 'Canadian', 'Female', 'Inspirational talk, though some examples were complex.'),
(1987, 'British', 'Non-binary', 'I liked the catering, but the timing was off.'),
(1992, 'Australian', 'Male', 'The speakers were knowledgeable but hard to understand due to their accents.'),
(1985, 'Indian', 'Female', 'The workshop was very useful and educational, but a bit too fast paced.'),
(1993, 'American', 'Male', 'The content was very dense and the room was too loud.'),
(1978, 'New Zealander', 'Female', 'The lecture was boring and the content was not very engaging.'),
(1984, 'South African', 'Non-binary', 'Very well organized event, but the room was a bit too bright.'),
(1991, 'Singaporean', 'Female', 'Great examples used, though the readability could be better.'),
(1985, 'Irish', 'Male', 'Very detailed and useful feedback, but needed more breaks.');

-- Generating AtomicFeedback entries related to UserFeedback
INSERT INTO AtomicFeedback (userFeedbackId, category, urgency, severity, impact, feedback) VALUES
(1, 'Problem', 20, 30, 40, 'too long and the room was too cold'),
(1, 'PositiveFeedback', 0, 0, 50, 'Great session'),
(2, 'Problem', 10, 20, 30, 'some examples were complex'),
(2, 'PositiveFeedback', 0, 0, 60, 'Inspirational talk'),
(3, 'Problem', 25, 15, 50, 'timing was off'),
(3, 'PositiveFeedback', 0, 0, 50, 'liked the catering'),
(4, 'Problem', 40, 35, 45, 'hard to understand due to their accents'),
(4, 'PositiveFeedback', 0, 0, 55, 'knowledgeable speakers'),
-- Continues for each UserFeedback
(5, 'Problem', 30, 20, 70, 'a bit too fast paced'),
(5, 'PositiveFeedback', 0, 0, 30, 'very useful and educational'),
(6, 'Problem', 20, 50, 80, 'content was very dense and the room was too loud'),
(7, 'Problem', 5, 10, 30, 'boring and not very engaging'),
(8, 'Problem', 10, 10, 40, 'room was a bit too bright'),
(8, 'PositiveFeedback', 0, 0, 60, 'well organized event'),
(9, 'Problem', 25, 25, 50, 'readability could be better'),
(9, 'PositiveFeedback', 0, 0, 50, 'Great examples used'),
(10, 'PositiveFeedback', 0, 0, 40, 'Very detailed and useful feedback');

-- Tag associations for each AtomicFeedback
-- This section randomly associates tags with AtomicFeedback entries
-- For simplicity, each AtomicFeedback gets 1 to 3 random tags
-- Assuming tag IDs range from 1 to 12 as per your initial insertions
INSERT INTO AtomicFeedback_Tag (atomicFeedbackId, tagId) VALUES
(1, 6), (1, 9), -- Feedback 1 gets tags 'RoomEnvironment' and 'Timing'
(2, 1), -- Feedback 2 gets tag 'Examples'
(3, 9), (3, 3), -- Feedback 3 gets tags 'Timing' and 'Tempo'
(4, 1), -- Feedback 4 gets tag 'Examples'
(5, 8), (5, 3), -- Feedback 5 gets tags 'Speakers' and 'Tempo'
(6, 4), -- Feedback 6 gets tag 'Usefulness'
(7, 11), -- Feedback 7 gets tag 'Boredom'
(8, 6), -- Feedback 8 gets tag 'RoomEnvironment'
(9, 10), -- Feedback 9 gets tag 'Readability'
(10, 4), (10, 2);