package com.toxmi.steadfast.core.utils;

import com.toxmi.steadfast.Steadfast;

public enum SQL {
    CREATE_CLAIM_TABLE(
            """
            CREATE TABLE IF NOT EXISTS claims (
                claimid TEXT PRIMARY KEY,
                claimname TEXT NOT NULL UNIQUE,
                claimpower INTEGER DEFAULT 0,
                claimchestlocation TEXT DEFAULT NULL,
                shieldcharge INTEGER DEFAULT 0,
                shieldstate TEXT CHECK (shieldstate IN ('INACTIVE', 'ACTIVE', 'CHARGING', 'ACTIVATING', 'RECHARGING')) DEFAULT 'INACTIVE',
                shieldmode TEXT CHECK (shieldmode IN ('MANUAL', 'AUTO')) DEFAULT 'AUTO'
            );
            
            CREATE TABLE IF NOT EXISTS claimchunks (
                chunkkey TEXT PRIMARY KEY,
                claimid TEXT NOT NULL REFERENCES claims(claimid) ON DELETE CASCADE
            );
            
            CREATE TABLE IF NOT EXISTS claim_artifacts (
                claimid TEXT NOT NULL REFERENCES claims(claimid) ON DELETE CASCADE,
                artifact TEXT NOT NULL,
                slot INTEGER NOT NULL,
                PRIMARY KEY (claimid, slot)
            );
            
            CREATE TABLE IF NOT EXISTS claim_power (
                claimid TEXT NOT NULL REFERENCES claims(claimid) ON DELETE CASCADE,
                power INTEGER DEFAULT 0,
                type TEXT CHECK (type IN ('SPAWNER', 'ARTIFACT', 'WEALTH')) NOT NULL
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS claims (
                claimid TEXT PRIMARY KEY,
                claimname TEXT NOT NULL UNIQUE,
                claimpower jsonb DEFAULT '{"total": 0, "spawner": 0, "artifacts": 0, "wealth": 0 }',
                claimchestlocation TEXT DEFAULT NULL,
                chunks TEXT[] DEFAULT ARRAY[]::TEXT[],
                shield jsonb DEFAULT '{"charge": 0, "state": "INACTIVE", "mode": "AUTO"}',
                artifacts jsonb DEFAULT '{"slot1": null, "slot2": null, "slot3": null}'
            );
            """
    ),
    CREATE_PLAYER_TABLE(
            """
            CREATE TABLE IF NOT EXISTS players (
                playerid TEXT PRIMARY KEY,
                claimid TEXT DEFAULT NULL REFERENCES claims(claimid) ON DELETE SET NULL,
                claimrole TEXT CHECK (claimrole IN ('Leader', 'Co-Leader', 'Officer', 'Member', 'Limited-Member')) DEFAULT NULL,
                coinbalance NUMERIC(30, 10) DEFAULT 0
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS players (
                playerid TEXT PRIMARY KEY,
                claimid TEXT DEFAULT NULL REFERENCES claims(claimid) ON DELETE SET NULL,
                claimrole TEXT CHECK (claimrole IN ('Leader', 'Co_Leader', 'Officer', 'Member', 'Limited_Member')) DEFAULT NULL,
                coinbalance NUMERIC(30, 10) DEFAULT 0
            );
            """
    ),
    CREATE_PLAYER_STATS_TABLE(
            """
            CREATE TABLE IF NOT EXISTS playerstats (
                playerid TEXT PRIMARY KEY REFERENCES players(playerid) ON DELETE CASCADE,
                kills INTEGER DEFAULT 0,
                deaths INTEGER DEFAULT 0,
                killstreak INTEGER DEFAULT 0,
                playtime INTEGER DEFAULT 0
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS playerstats (
                playerid TEXT PRIMARY KEY REFERENCES players(playerid) ON DELETE CASCADE,
                kills INTEGER DEFAULT 0,
                deaths INTEGER DEFAULT 0,
                killstreak INTEGER DEFAULT 0,
                playtime BIGINT DEFAULT 0
            );
            """
    ),
    GET_ALL_PLAYERS(
            """
            SELECT * FROM players;
            """,
            """
            SELECT * FROM players;
            """
    ),
    GET_PLAYER(
            """
            SELECT * FROM players WHERE playerid = ?;
            """,
            """
            SELECT * FROM players WHERE playerid = ?;
            """
    ),
    INSERT_PLAYER(
            """
            INSERT INTO players (playerid, playername) VALUES (?, ?);
            """,
            """
            INSERT INTO players (playerid, playername) VALUES (?, ?);
            """
    ),
    UPDATE_PLAYER_CLAIM(
            """
            UPDATE players SET claimid = ?  WHERE playerid = ?;
            """,
            """
            UPDATE players SET claimid = ? WHERE playerid = ?;
            """
    ),
    UPDATE_PLAYER_ROLE(
            """
            UPDATE players SET claimrole = ? WHERE playerid = ?;
            """,
            """
            UPDATE players SET claimrole = ? WHERE playerid = ?;
            """
    ),
    UPDATE_PLAYER_COIN_BALANCE(
            """
            UPDATE players SET coinbalance = ? WHERE playerid = ?;
            """,
            """
            UPDATE players SET coinbalance = ? WHERE playerid = ?;
            """
    ),
    GET_PLAYER_STATS(
            """
            SELECT * FROM playerstats WHERE playerid = ?;
            """,
            """
            SELECT * FROM playerstats WHERE playerid = ?;
            """
    ),
    INSERT_PLAYER_STATS(
            """
            INSERT INTO playerstats (playerid) VALUES (?);
            """,
            """
            INSERT INTO playerstats (playerid) VALUES (?);
            """
    ),
    UPDATE_PLAYER_STATS_KILLS(
            """
            UPDATE playerstats SET kills = ? WHERE playerid = ?;
            """,
            """
            UPDATE playerstats SET kills = ? WHERE playerid = ?;
            """
    ),
    UPDATE_PLAYER_STATS_DEATHS(
            """
            UPDATE playerstats SET deaths = ? WHERE playerid = ?;
            """,
            """
            UPDATE playerstats SET deaths = ? WHERE playerid = ?;
            """
    ),
    UPDATE_PLAYER_STATS_KILL_STREAK(
            """
            UPDATE playerstats SET killstreak = ? WHERE playerid = ?;
            """,
            """
            UPDATE playerstats SET killstreak = ? WHERE playerid = ?;
            """
    ),
    UPDATE_PLAYER_STATS_PLAY_TIME(
            """
            UPDATE playerstats SET playtime = ? WHERE playerid = ?;
            """,
            """
            UPDATE playerstats SET playtime = ? WHERE playerid = ?;
            """
    ),
    GET_ALL_CLAIM_IDS (
            """
            SELECT claimid FROM claims;
            """,
            """
            SELECT claimid FROM claims
            """
    ),
    GET_CLAIM(
            """
            SELECT
                c.* ,
                MAX(CASE WHEN ca.slot = 1 THEN ca.artifact END) AS artifact1,
                MAX(CASE WHEN ca.slot = 2 THEN ca.artifact END) AS artifact2,
                MAX(CASE WHEN ca.slot = 3 THEN ca.artifact END) AS artifact3,
                MAX(CASE WHEN cp.type = 'ARTIFACT' THEN cp.power END) AS artifactpower,
                MAX(CASE WHEN cp.type = 'SPAWNER' THEN cp.power END) AS spawnerpower,
                MAX(CASE WHEN cp.type = 'WEALTH' THEN cp.power END) AS wealthpower
            FROM claims c
                LEFT JOIN claim_artifacts ca
                    ON ca.claimid = c.claimid
                LEFT JOIN claim_power cp
                    ON cp.claimid = c.claimid
            WHERE claimid = ?
            GROUP BY
                c.claimid,
                c.claimname;
            """,
            """
            SELECT
                claimname,
                claimchestlocation,
                claimpower -> 'total' AS claimpower,
                claimpower -> 'spawner' AS spawnerpower,
                claimpower -> 'wealth' AS wealthpower,
                claimpower -> 'artifact' AS artifactpower,
                shield -> 'charge' AS shieldcharge,
                shield -> 'state' AS shieldstate,
                shield -> 'mode' AS shieldmode,
                artifacts -> 'slot1' AS artifact1,
                artifacts -> 'slot2' AS artifact2,
                artifacts -> 'slot3' AS artifact3
            FROM claims WHERE claimid = ?;
            """
    ),
    GET_CLAIM_CHUNKS(
            """
            SELECT chunkkey FROM claimchunks WHERE claimid = ?;
            """,
            """
            SELECT chunk AS chunkkey FROM claims
            CROSS JOIN unnest(chunks) WITH ORDINALITY AS u(chunk) WHERE claimid = ?;
            """
    ),
    INSERT_CLAIM(
            """
            INSERT INTO claims (claimid, claimname, claimchestlocation) VALUES (?, ?, ?);
            """,
            """
            INSERT INTO claims (claimid, claimname, claimchestlocation) VALUES (?, ?, ?);
            """
    ),
    UPDATE_CLAIM_NAME (
            """
            UPDATE claims SET claimname = ? WHERE claimid = ?;
            """,
            """
            UPDATE claims SET claimname = ? WHERE claimid = ?;
            """

    ),
    UPDATE_CLAIM_CHEST_LOCATION(
            """
            UPDATE claims SET claimchestlocation = ? WHERE claimid = ?;
            """,
            """
            UPDATE claims SET claimchestlocation = ? WHERE claimid = ?;
            """
    ),
    INSERT_CLAIM_CHUNK(
            """
            INSERT INTO claimchunks (chunkkey, claimid) VALUES (?, ?);
            """,
            """
            UPDATE claims SET chunks = array_append(chunks, ?) WHERE claimid = ?;
            """
    ),
    DELETE_CLAIM_CHUNK(
            """
            DELETE FROM claimchunks WHERE chunkkey = ?;
            """,
            """
            UPDATE claims SET chunks = array_remove(chunks, ?) WHERE claimid = ?;
            """
    ),
    GET_CLAIM_MEMBERS(
            """
            SELECT playerid, claimrole FROM players WHERE claimid = ?;
            """,
            """
            SELECT playerid, claimrole FROM players WHERE claimid = ?;
            """
    ),
    CHANGE_CLAIM_SHIELD_STATE (
            """
            UPDATE claims SET shieldstate = ? WHERE claimid = ?;
            """,
            """
            UPDATE claims SET shield = jsonb_set(shield, '{state}', to_jsonb(?::text)) WHERE claimid = ?;
            """
    ),
    UPDATE_CLAIM_SHIELD_CHARGE (
            """
            UPDATE claims SET shieldcharge = ? WHERE claimid = ?;
            """,
            """
            UPDATE claims SET shield = jsonb_set(shield, '{charge}', to_jsonb(?::INTEGER)) WHERE claimid = ?;
            """
    ),
    CHANGE_CLAIM_SHIELD_MODE (
            """
            UPDATE claims SET shieldmode = ? WHERE claimid = ?;
            """,
            """
            UPDATE claims SET shield = jsonb_set(shield, '{mode}', to_jsonb(?::text)) WHERE claimid = ?;
            """
    ),
    SET_ARTIFACT (
            """
            INSERT INTO claim_artifacts (slot, artifact, claimid)
            VALUES (?, ?, ?)
            ON CONFLICT (claimid, slot)
                DO UPDATE SET artifact = excluded.artifact;
            """,
            """
            UPDATE claims
                SET artifacts = jsonb_set(artifacts, ARRAY['slot' || ?::text], to_jsonb(?::text))
                WHERE claimid = ?::text;
            """
    ),
    SET_POWER_FROM_SOURCE (
            """
            INSERT INTO claim_power (type, power, claimid)
            VALUES (?, ?, ?)
            ON CONFLICT (claimid, type)
                DO UPDATE SET power = excluded.power;
            """,
            """
            UPDATE claims
                SET claimpower = jsonb_set(claimpower, ARRAY[?::text], to_jsonb(?))
                WHERE claimid = ?::text;
            """
    ),
    SET_TOTAL_POWER (
            """
            UPDATE claims SET claimpower = ? WHERE claimid = ?;
            """,
            """
            UPDATE claims SET claimpower = jsonb_set(claimpower, '{total}', to_jsonb(?)) WHERE claimid = ?;
            """
    ),
    SET_SHIELD_CHARGE (
            """
            UPDATE claims SET shieldcharge = ? WHERE claimid = ?;
            """,
            """
            UPDATE claims SET shield = jsonb_set(shield, '{charge}', to_jsonb(?)) WHERE claimid = ?;
            """
    ),
    DELETE_CLAIM (
            """
            DELETE FROM claims WHERE claimid = ?;
            """,
            """
            DELETE FROM claims WHERE claimid = ?;
            """
    );

    private final String sqliteQuery;
    private final String postgresqlQuery;

    SQL(String sqlite, String postgresql) {
        this.sqliteQuery = sqlite;
        this.postgresqlQuery = postgresql;
    }

    @Override
    public String toString() {
        return Steadfast.get().getDatabaseType().equalsIgnoreCase("sqlite") ? sqliteQuery : postgresqlQuery;
    }
}
