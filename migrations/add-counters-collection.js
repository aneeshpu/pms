db.counters.insert({
    seq: 10000
});

function getNextSequence(){
    var ret = db.counters.findAndModify({ 
        update: { $inc: {seq: 1}}
    });
    return ret.seq;
}
