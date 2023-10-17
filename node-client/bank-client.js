const grpc = require('@grpc/grpc-js')
const protoLoader = require('@grpc/proto-loader')

const packageDef = protoLoader.loadSync('proto/bank-service.proto')

const protoDesc = grpc.loadPackageDefinition(packageDef)

const client = new protoDesc.BankService('localhost:6565', grpc.credentials.createInsecure())

client.withdraw({accountNumber: 4, amount: 50}, (err, money) => {
    if(err){
        console.error("something bad happend")
    } else{
        console.log("Received: "+ money.amount)
    }
})