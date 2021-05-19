pipeline {
    agent any

    parameters {
        gitParameter branchFilter: 'origin/(.*)', defaultValue: 'main', name: 'MANUAL_GIT_BRANCH', type: 'PT_BRANCH'
        choice name: 'IMAGE_TYPE',
            choices: ['release', 'test'],
            description: 'image type'
    }

    options {
        ansiColor('css')
        timestamps()
    }

    environment {
        DOCKER_URL = 'https://registry.hub.docker.com'
        DOCKER_CREDENTIALS_ID = 'docker-hub-token-symbolserverbot'
    }

    stages {
        stage('print env') {
            steps {
                echo """
                            env.GIT_BRANCH: ${env.GIT_BRANCH}
                         MANUAL_GIT_BRANCH: ${MANUAL_GIT_BRANCH}

                                IMAGE_TYPE: ${IMAGE_TYPE}
                """
            }
        }
        stage('prepare Dockerfile') {
            steps {
                script {
                    ubuntu_version = readProperties(file: './scripts/build/versions.properties')['ubuntu']

                    dockerfile_template = "./scripts/build/templates/${params.IMAGE_TYPE.capitalize()}BaseImage.Dockerfile"
                    dockerfile_contents = readFile(file: dockerfile_template)
                    dockerfile_contents = dockerfile_contents.replaceAll('\\{\\{BASE_IMAGE\\}\\}', "ubuntu:${ubuntu_version}")

                    writeFile(file: 'Dockerfile', text: dockerfile_contents)
                }
            }
        }
        stage('print Dockerfile') {
            steps {
                sh '''
                    echo '*** Dockerfile ***'
                    cat Dockerfile
                '''
            }
        }

        stage('build image') {
            steps {
                script {
                    docker_image = docker.build "symbolplatform/symbol-server-${params.IMAGE_TYPE}-base:latest"
                    docker.withRegistry(DOCKER_URL, DOCKER_CREDENTIALS_ID) {
                        docker_image.push()
                    }
                }
            }
        }
    }
}
