#!/usr/bin/groovy

/**
 * Adds a status on the PR in github
 * @param hash - The pull-request head hash.
 * @param project - The github project , should be string.
 */
def call(String status, String hash, String project) {
    withCredentials([[$class: 'StringBinding', credentialsId: 'github_oath_token', variable: 'GITHUB_ACCESS_TOKEN']]) {
        def githubToken = "${GITHUB_ACCESS_TOKEN}"
        def apiUrl = new URL("https://api.github.com/repos/${project}/statuses/${hash}")
        echo "adding ${status} to ${apiUrl}"
        try {
            def HttpURLConnection connection = apiUrl.openConnection()
            if (githubToken.length() > 0) {
                connection.setRequestProperty("Authorization", "Bearer ${githubToken}")
            }
            connection.setRequestMethod("POST")
            connection.setDoOutput(true)
            connection.connect()

            def body = "{\"state\": \"${status}\", \"context\": \"continuous-integration/jenkins/default\"}"

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())
            writer.write(body)
            writer.flush()

            // execute the POST request
            new InputStreamReader(connection.getInputStream())

            connection.disconnect()
        } catch (err) {
            echo "ERROR  ${err}"
        }
    }
}
