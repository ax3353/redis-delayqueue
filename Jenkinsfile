pipeline{
    //指定运行此流水线的节点
    agent any

    parameters {
        string(name: 'eventdriver', defaultValue: '0.0.1', description: 'eventdriver 版本号')
    }

    //流水线的阶段
    stages{
        stage("Clean"){
            agent any
            steps {
                echo "Clean"
            }
        }

        stage('Mvn Build'){
            agent any
            steps {
                echo "Mvn Build"
            }
        }

        stage('Docker Build') {
            agent any
            steps {
                echo "Docker Build"
            }
        }

        stage('Deploy') {
            agent any
            steps {
                echo "Deploy"
            }
        }
    }

    post {
        always{
            script{
                println("结束")
            }
        }

        success{
            script{
                println("成功")
            }

        }
        failure{
            script{
                println("失败")
            }
        }

        aborted{
            script{
                println("取消")
            }
        }
    }
}