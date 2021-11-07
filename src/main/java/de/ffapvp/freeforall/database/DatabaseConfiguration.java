/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseConfiguration implements Serializable {

    private String host = "localhost";
    private int port = 27019;

    private boolean login = false;

    private String user = "", database = "ffa4fun", password = "";

}
