package org.uzdiz.chain_of_responsibility;

import org.uzdiz.singleton.HrvatskeZeljeznice;

public class ChainCreator {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public ChainCreator(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    public CommandHandler buildCommandHandlerChain() {
        CommandHandler ipHandler = new IPHandler(hrvatskeZeljeznice);
        CommandHandler ispHandler = new ISPHandler(hrvatskeZeljeznice);
        CommandHandler isi2sHandler = new ISI2SHandler(hrvatskeZeljeznice);
        CommandHandler ikHandler = new IKHandler(hrvatskeZeljeznice);
        CommandHandler ivHandler = new IVHandler(hrvatskeZeljeznice);
        CommandHandler ievHandler = new IEVHandler(hrvatskeZeljeznice);
        CommandHandler ievdHandler = new IEVDHandler(hrvatskeZeljeznice);
        CommandHandler ivrvHandler = new IVRVHandler(hrvatskeZeljeznice);
        CommandHandler dkHandler = new DKHandler(hrvatskeZeljeznice);
        CommandHandler pkHandler = new PKHandler(hrvatskeZeljeznice);
        CommandHandler dpkHandler = new DPKHandler(hrvatskeZeljeznice);
        CommandHandler svvHandler = new SVVHandler(hrvatskeZeljeznice);
        CommandHandler dsvHandler = new DSVHandler(hrvatskeZeljeznice);
        CommandHandler ivi2sHandler = new IVI2SHandler(hrvatskeZeljeznice);
        CommandHandler cvpHandler = new CVPHandler(hrvatskeZeljeznice);
        CommandHandler kkpv2sHandler = new KKPV2SHandler(hrvatskeZeljeznice);
        CommandHandler ikkpvHandler = new IKKPVHandler(hrvatskeZeljeznice);
        CommandHandler ukp2sHandler = new UKP2SHandler(hrvatskeZeljeznice);
        CommandHandler psp2sHandler = new PSP2SHandler(hrvatskeZeljeznice);
        CommandHandler irpsHandler = new IRPSHandler(hrvatskeZeljeznice);

        ipHandler.setNextHandler(ispHandler);
        ispHandler.setNextHandler(isi2sHandler);
        isi2sHandler.setNextHandler(ikHandler);
        ikHandler.setNextHandler(ivHandler);
        ivHandler.setNextHandler(ievHandler);
        ievHandler.setNextHandler(ievdHandler);
        ievdHandler.setNextHandler(ivrvHandler);
        ivrvHandler.setNextHandler(dkHandler);
        dkHandler.setNextHandler(pkHandler);
        pkHandler.setNextHandler(dpkHandler);
        dpkHandler.setNextHandler(svvHandler);
        svvHandler.setNextHandler(dsvHandler);
        dsvHandler.setNextHandler(ivi2sHandler);
        ivi2sHandler.setNextHandler(cvpHandler);
        cvpHandler.setNextHandler(kkpv2sHandler);
        kkpv2sHandler.setNextHandler(ikkpvHandler);
        ikkpvHandler.setNextHandler(ukp2sHandler);
        ukp2sHandler.setNextHandler(psp2sHandler);
        psp2sHandler.setNextHandler(irpsHandler);

        return ipHandler;
    }
}