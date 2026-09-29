package com.example.domain.commentary.dialogues

import com.example.domain.commentary.CommentaryDialogue
import com.example.domain.commentary.Commentator

object MatchDialogues {

    // --- OVER COMPLETE DIALOGUES (30 Short Variations, <= 1 sec) ---
    val OVER_COMPLETE_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("OVR_01", listOf(CommentaryDialogue(Commentator.RAHUL, "ओवर समाप्त!"))),
        DialogueTemplate("OVR_02", listOf(CommentaryDialogue(Commentator.NEHA, "एक और ओवर पूरा!"))),
        DialogueTemplate("OVR_03", listOf(CommentaryDialogue(Commentator.RAHUL, "ओवर की समाप्ति!"))),
        DialogueTemplate("OVR_04", listOf(CommentaryDialogue(Commentator.NEHA, "किफायती ओवर खत्म!"))),
        DialogueTemplate("OVR_05", listOf(CommentaryDialogue(Commentator.RAHUL, "बढ़िया ओवर समाप्त हुआ!"))),
        DialogueTemplate("OVR_06", listOf(CommentaryDialogue(Commentator.NEHA, "गेंदबाज़ का अच्छा ओवर!"))),
        DialogueTemplate("OVR_07", listOf(CommentaryDialogue(Commentator.RAHUL, "छह गेंदें पूरी!"))),
        DialogueTemplate("OVR_08", listOf(CommentaryDialogue(Commentator.NEHA, "ओवर खत्म, नया ओवर!"))),
        DialogueTemplate("OVR_09", listOf(CommentaryDialogue(Commentator.RAHUL, "सफल ओवर की समाप्ति!"))),
        DialogueTemplate("OVR_10", listOf(CommentaryDialogue(Commentator.NEHA, "ओवर मुकम्मल हुआ!"))),
        DialogueTemplate("OVR_11", listOf(CommentaryDialogue(Commentator.RAHUL, "शानदार ओवर खत्म!"))),
        DialogueTemplate("OVR_12", listOf(CommentaryDialogue(Commentator.NEHA, "ओवर का अंत!"))),
        DialogueTemplate("OVR_13", listOf(CommentaryDialogue(Commentator.RAHUL, "रन रोकने वाला ओवर!"))),
        DialogueTemplate("OVR_14", listOf(CommentaryDialogue(Commentator.NEHA, "छह गेंदें समाप्त!"))),
        DialogueTemplate("OVR_15", listOf(CommentaryDialogue(Commentator.RAHUL, "ओवर पूरा हुआ!"))),
        DialogueTemplate("OVR_16", listOf(CommentaryDialogue(Commentator.NEHA, "गेंदबाज़ ने ओवर निकाला!"))),
        DialogueTemplate("OVR_17", listOf(CommentaryDialogue(Commentator.RAHUL, "सटीक ओवर समाप्त!"))),
        DialogueTemplate("OVR_18", listOf(CommentaryDialogue(Commentator.NEHA, "कंट्रोल भरा ओवर खत्म!"))),
        DialogueTemplate("OVR_19", listOf(CommentaryDialogue(Commentator.RAHUL, "ओवर पूरा, छोर बदला!"))),
        DialogueTemplate("OVR_20", listOf(CommentaryDialogue(Commentator.NEHA, "अंतिम गेंद समाप्त!"))),
        DialogueTemplate("OVR_21", listOf(CommentaryDialogue(Commentator.RAHUL, "दबाव भरा ओवर खत्म!"))),
        DialogueTemplate("OVR_22", listOf(CommentaryDialogue(Commentator.NEHA, "अनुशासित ओवर की समाप्ति!"))),
        DialogueTemplate("OVR_23", listOf(CommentaryDialogue(Commentator.RAHUL, "ओवर पूरा हुआ!"))),
        DialogueTemplate("OVR_24", listOf(CommentaryDialogue(Commentator.NEHA, "सख्त ओवर समाप्त!"))),
        DialogueTemplate("OVR_25", listOf(CommentaryDialogue(Commentator.RAHUL, "नया ओवर शुरू होगा!"))),
        DialogueTemplate("OVR_26", listOf(CommentaryDialogue(Commentator.NEHA, "शानदार छह गेंदें पूरी!"))),
        DialogueTemplate("OVR_27", listOf(CommentaryDialogue(Commentator.RAHUL, "ओवर का खेल समाप्त!"))),
        DialogueTemplate("OVR_28", listOf(CommentaryDialogue(Commentator.NEHA, "कमाल का ओवर खत्म हुआ!"))),
        DialogueTemplate("OVR_29", listOf(CommentaryDialogue(Commentator.RAHUL, "ओवर समाप्त!"))),
        DialogueTemplate("OVR_30", listOf(CommentaryDialogue(Commentator.NEHA, "एक और ओवर समाप्त हुआ!")))
    )

    // --- MATCH WIN DIALOGUES (50 Short Variations, <= 1 sec) ---
    val MATCH_WIN_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("WIN_01", listOf(CommentaryDialogue(Commentator.RAHUL, "और ये जीत!"))),
        DialogueTemplate("WIN_02", listOf(CommentaryDialogue(Commentator.NEHA, "शानदार जीत!"))),
        DialogueTemplate("WIN_03", listOf(CommentaryDialogue(Commentator.RAHUL, "मुकाबला जीत लिया!"))),
        DialogueTemplate("WIN_04", listOf(CommentaryDialogue(Commentator.NEHA, "जीत की गूंज!"))),
        DialogueTemplate("WIN_05", listOf(CommentaryDialogue(Commentator.RAHUL, "कमाल की जीत!"))),
        DialogueTemplate("WIN_06", listOf(CommentaryDialogue(Commentator.NEHA, "ऐतिहासिक जीत दर्ज की!"))),
        DialogueTemplate("WIN_07", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच अपने नाम किया!"))),
        DialogueTemplate("WIN_08", listOf(CommentaryDialogue(Commentator.NEHA, "जीत का शानदार जश्न!"))),
        DialogueTemplate("WIN_09", listOf(CommentaryDialogue(Commentator.RAHUL, "शानदार जीत हासिल!"))),
        DialogueTemplate("WIN_10", listOf(CommentaryDialogue(Commentator.NEHA, "मैच जीत लिया है!"))),
        DialogueTemplate("WIN_11", listOf(CommentaryDialogue(Commentator.RAHUL, "शानदार तरीके से जीते!"))),
        DialogueTemplate("WIN_12", listOf(CommentaryDialogue(Commentator.NEHA, "जीत पर मुहर लगा दी!"))),
        DialogueTemplate("WIN_13", listOf(CommentaryDialogue(Commentator.RAHUL, "विजेता बने! बधाई!"))),
        DialogueTemplate("WIN_14", listOf(CommentaryDialogue(Commentator.NEHA, "धमाकेदार जीत!"))),
        DialogueTemplate("WIN_15", listOf(CommentaryDialogue(Commentator.RAHUL, "क्या मुकाबला जीता है!"))),
        DialogueTemplate("WIN_16", listOf(CommentaryDialogue(Commentator.NEHA, "लाजवाब जीत!"))),
        DialogueTemplate("WIN_17", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत का परचम लहराया!"))),
        DialogueTemplate("WIN_18", listOf(CommentaryDialogue(Commentator.NEHA, "जीत की खुशी!"))),
        DialogueTemplate("WIN_19", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच में शानदार फतह!"))),
        DialogueTemplate("WIN_20", listOf(CommentaryDialogue(Commentator.NEHA, "शानदार विजयी पल!"))),
        DialogueTemplate("WIN_21", listOf(CommentaryDialogue(Commentator.RAHUL, "मुकाबला खत्म, शानदार जीत!"))),
        DialogueTemplate("WIN_22", listOf(CommentaryDialogue(Commentator.NEHA, "विजेता टीम का जलवा!"))),
        DialogueTemplate("WIN_23", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत के साथ अंत!"))),
        DialogueTemplate("WIN_24", listOf(CommentaryDialogue(Commentator.NEHA, "तालियों के बीच जीत!"))),
        DialogueTemplate("WIN_25", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत हासिल कर ली!"))),
        DialogueTemplate("WIN_26", listOf(CommentaryDialogue(Commentator.NEHA, "मैच जीत लिया!"))),
        DialogueTemplate("WIN_27", listOf(CommentaryDialogue(Commentator.RAHUL, "गर्व से भरी जीत!"))),
        DialogueTemplate("WIN_28", listOf(CommentaryDialogue(Commentator.NEHA, "जीत का रोमांच!"))),
        DialogueTemplate("WIN_29", listOf(CommentaryDialogue(Commentator.RAHUL, "दमदार खेल, शानदार जीत!"))),
        DialogueTemplate("WIN_30", listOf(CommentaryDialogue(Commentator.NEHA, "जीत पक्की की!"))),
        DialogueTemplate("WIN_31", listOf(CommentaryDialogue(Commentator.RAHUL, "शानदार फिनिश, जीत मिली!"))),
        DialogueTemplate("WIN_32", listOf(CommentaryDialogue(Commentator.NEHA, "विजेता की दहाड़!"))),
        DialogueTemplate("WIN_33", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत का परचम!"))),
        DialogueTemplate("WIN_34", listOf(CommentaryDialogue(Commentator.NEHA, "मैच जीत गए!"))),
        DialogueTemplate("WIN_35", listOf(CommentaryDialogue(Commentator.RAHUL, "विजयरथ आगे बढ़ा!"))),
        DialogueTemplate("WIN_36", listOf(CommentaryDialogue(Commentator.NEHA, "जीत का शानदार पल!"))),
        DialogueTemplate("WIN_37", listOf(CommentaryDialogue(Commentator.RAHUL, "शानदार जीत पर बधाई!"))),
        DialogueTemplate("WIN_38", listOf(CommentaryDialogue(Commentator.NEHA, "जीत का आनंद लीजिए!"))),
        DialogueTemplate("WIN_39", listOf(CommentaryDialogue(Commentator.RAHUL, "अंतिम गेंद पर जीत!"))),
        DialogueTemplate("WIN_40", listOf(CommentaryDialogue(Commentator.NEHA, "जीत का स्वाद चखा!"))),
        DialogueTemplate("WIN_41", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत दर्ज कर ली!"))),
        DialogueTemplate("WIN_42", listOf(CommentaryDialogue(Commentator.NEHA, "कमाल का खेल, जीत मिली!"))),
        DialogueTemplate("WIN_43", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत का डंका बजाया!"))),
        DialogueTemplate("WIN_44", listOf(CommentaryDialogue(Commentator.NEHA, "जीत मुबारक हो!"))),
        DialogueTemplate("WIN_45", listOf(CommentaryDialogue(Commentator.RAHUL, "शानदार जीत हासिल की!"))),
        DialogueTemplate("WIN_46", listOf(CommentaryDialogue(Commentator.NEHA, "मैच अपने कब्जे में!"))),
        DialogueTemplate("WIN_47", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत की खूबसूरत तस्वीर!"))),
        DialogueTemplate("WIN_48", listOf(CommentaryDialogue(Commentator.NEHA, "शानदार जीत!"))),
        DialogueTemplate("WIN_49", listOf(CommentaryDialogue(Commentator.RAHUL, "मुकाबला जीत लिया!"))),
        DialogueTemplate("WIN_50", listOf(CommentaryDialogue(Commentator.NEHA, "जीत का जश्न शुरू!")))
    )

    // --- MATCH LOSS DIALOGUES (30 Short Variations, <= 1 sec) ---
    val MATCH_LOSS_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("LOS_01", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच गंवा दिया!"))),
        DialogueTemplate("LOS_02", listOf(CommentaryDialogue(Commentator.NEHA, "निराशाजनक हार!"))),
        DialogueTemplate("LOS_03", listOf(CommentaryDialogue(Commentator.RAHUL, "हार का सामना करना पड़ा!"))),
        DialogueTemplate("LOS_04", listOf(CommentaryDialogue(Commentator.NEHA, "कड़ी टक्कर में हारे!"))),
        DialogueTemplate("LOS_05", listOf(CommentaryDialogue(Commentator.RAHUL, "दिन अच्छा नहीं रहा!"))),
        DialogueTemplate("LOS_06", listOf(CommentaryDialogue(Commentator.NEHA, "मुकाबला हाथ से निकला!"))),
        DialogueTemplate("LOS_07", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत से चूक गए!"))),
        DialogueTemplate("LOS_08", listOf(CommentaryDialogue(Commentator.NEHA, "अफसोस, हार मिली!"))),
        DialogueTemplate("LOS_09", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच में हार का मुंह देखा!"))),
        DialogueTemplate("LOS_10", listOf(CommentaryDialogue(Commentator.NEHA, "कड़ा संघर्ष, लेकिन हार!"))),
        DialogueTemplate("LOS_11", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच समाप्त, हार मिली!"))),
        DialogueTemplate("LOS_12", listOf(CommentaryDialogue(Commentator.NEHA, "पराजित हुए, पर लड़े!"))),
        DialogueTemplate("LOS_13", listOf(CommentaryDialogue(Commentator.RAHUL, "हार से सबक मिलेगा!"))),
        DialogueTemplate("LOS_14", listOf(CommentaryDialogue(Commentator.NEHA, "किस्मत ने साथ नहीं दिया!"))),
        DialogueTemplate("LOS_15", listOf(CommentaryDialogue(Commentator.RAHUL, "प्रतिद्वंद्वी भारी पड़ा!"))),
        DialogueTemplate("LOS_16", listOf(CommentaryDialogue(Commentator.NEHA, "हार का कड़वा घूंट!"))),
        DialogueTemplate("LOS_17", listOf(CommentaryDialogue(Commentator.RAHUL, "मुकाबला समाप्त!"))),
        DialogueTemplate("LOS_18", listOf(CommentaryDialogue(Commentator.NEHA, "अगले मैच में वापसी करेंगे!"))),
        DialogueTemplate("LOS_19", listOf(CommentaryDialogue(Commentator.RAHUL, "निराशा हाथ लगी!"))),
        DialogueTemplate("LOS_20", listOf(CommentaryDialogue(Commentator.NEHA, "मुकाबला हार गए!"))),
        DialogueTemplate("LOS_21", listOf(CommentaryDialogue(Commentator.RAHUL, "जीत की दहलीज पर चूके!"))),
        DialogueTemplate("LOS_22", listOf(CommentaryDialogue(Commentator.NEHA, "विरोधी टीम की जीत!"))),
        DialogueTemplate("LOS_23", listOf(CommentaryDialogue(Commentator.RAHUL, "कठिन मुकाबला, पर हार!"))),
        DialogueTemplate("LOS_24", listOf(CommentaryDialogue(Commentator.NEHA, "हार स्वीकार करनी होगी!"))),
        DialogueTemplate("LOS_25", listOf(CommentaryDialogue(Commentator.RAHUL, "कमियों पर काम करना होगा!"))),
        DialogueTemplate("LOS_26", listOf(CommentaryDialogue(Commentator.NEHA, "दिल तोड़ने वाली हार!"))),
        DialogueTemplate("LOS_27", listOf(CommentaryDialogue(Commentator.RAHUL, "आज जीत नसीब नहीं हुई!"))),
        DialogueTemplate("LOS_28", listOf(CommentaryDialogue(Commentator.NEHA, "संघर्ष भरा मैच, पर हार!"))),
        DialogueTemplate("LOS_29", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच गंवाया!"))),
        DialogueTemplate("LOS_30", listOf(CommentaryDialogue(Commentator.NEHA, "हार मिली, वापसी होगी!")))
    )

    // --- TOSS DIALOGUES (20 Short Variations, <= 1 sec) ---
    val TOSS_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("TSS_01", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस का सिक्का उछला!"))),
        DialogueTemplate("TSS_02", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस जीत लिया!"))),
        DialogueTemplate("TSS_03", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस का अहम फैसला!"))),
        DialogueTemplate("TSS_04", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस जीतकर बल्लेबाज़ी!"))),
        DialogueTemplate("TSS_05", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस जीतकर गेंदबाज़ी!"))),
        DialogueTemplate("TSS_06", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस का फैसला हुआ!"))),
        DialogueTemplate("TSS_07", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस जीता, बड़ा फैसला!"))),
        DialogueTemplate("TSS_08", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस पर सिक्का गिरा!"))),
        DialogueTemplate("TSS_09", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस का रोमांच!"))),
        DialogueTemplate("TSS_10", listOf(CommentaryDialogue(Commentator.NEHA, "सिक्के की बाजी जीती!"))),
        DialogueTemplate("TSS_11", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस जीतकर रणनीति बनाई!"))),
        DialogueTemplate("TSS_12", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस का फैसला सामने!"))),
        DialogueTemplate("TSS_13", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस जीता, खुशी की लहर!"))),
        DialogueTemplate("TSS_14", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस की पहली जंग जीती!"))),
        DialogueTemplate("TSS_15", listOf(CommentaryDialogue(Commentator.RAHUL, "सिक्के का रुख तय हुआ!"))),
        DialogueTemplate("TSS_16", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस अपने नाम किया!"))),
        DialogueTemplate("TSS_17", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस का परिणाम आ गया!"))),
        DialogueTemplate("TSS_18", listOf(CommentaryDialogue(Commentator.NEHA, "बड़ा टॉस, बड़ा मैच!"))),
        DialogueTemplate("TSS_19", listOf(CommentaryDialogue(Commentator.RAHUL, "टॉस जीतकर बैटिंग!"))),
        DialogueTemplate("TSS_20", listOf(CommentaryDialogue(Commentator.NEHA, "टॉस जीतकर बॉलिंग!")))
    )

    // --- MATCH START DIALOGUES (20 Short Variations, <= 1 sec) ---
    val MATCH_START_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("MST_01", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच शुरू होने जा रहा है!"))),
        DialogueTemplate("MST_02", listOf(CommentaryDialogue(Commentator.NEHA, "पहली गेंद का इंतज़ार!"))),
        DialogueTemplate("MST_03", listOf(CommentaryDialogue(Commentator.RAHUL, "खिलाड़ी मैदान पर उतरे!"))),
        DialogueTemplate("MST_04", listOf(CommentaryDialogue(Commentator.NEHA, "शानदार मुकाबले का आगाज!"))),
        DialogueTemplate("MST_05", listOf(CommentaryDialogue(Commentator.RAHUL, "बल्लेबाज़ क्रीज पर पहुंचे!"))),
        DialogueTemplate("MST_06", listOf(CommentaryDialogue(Commentator.NEHA, "गेंदबाज़ तैयार रनअप पर!"))),
        DialogueTemplate("MST_07", listOf(CommentaryDialogue(Commentator.RAHUL, "मुकाबला शुरू होने को है!"))),
        DialogueTemplate("MST_08", listOf(CommentaryDialogue(Commentator.NEHA, "स्टेडियम में जबरदस्त शोर!"))),
        DialogueTemplate("MST_09", listOf(CommentaryDialogue(Commentator.RAHUL, "अंपायर ने इशारा किया!"))),
        DialogueTemplate("MST_10", listOf(CommentaryDialogue(Commentator.NEHA, "खेल शुरू होता हुआ!"))),
        DialogueTemplate("MST_11", listOf(CommentaryDialogue(Commentator.RAHUL, "गेंद और बल्ले की जंग!"))),
        DialogueTemplate("MST_12", listOf(CommentaryDialogue(Commentator.NEHA, "दर्शकों में भारी उत्साह!"))),
        DialogueTemplate("MST_13", listOf(CommentaryDialogue(Commentator.RAHUL, "पहली गेंद के लिए तैयार!"))),
        DialogueTemplate("MST_14", listOf(CommentaryDialogue(Commentator.NEHA, "रोमांचक मुकाबले की शुरुआत!"))),
        DialogueTemplate("MST_15", listOf(CommentaryDialogue(Commentator.RAHUL, "मैदान सज चुका है!"))),
        DialogueTemplate("MST_16", listOf(CommentaryDialogue(Commentator.NEHA, "मैच की पहली गेंद आने वाली है!"))),
        DialogueTemplate("MST_17", listOf(CommentaryDialogue(Commentator.RAHUL, "दोनों टीमें तैयार!"))),
        DialogueTemplate("MST_18", listOf(CommentaryDialogue(Commentator.NEHA, "धड़कनें तेज हैं!"))),
        DialogueTemplate("MST_19", listOf(CommentaryDialogue(Commentator.RAHUL, "शुरू होता है महामुकाबला!"))),
        DialogueTemplate("MST_20", listOf(CommentaryDialogue(Commentator.NEHA, "खेल का रोमांच शुरू!")))
    )

    // --- SITUATIONAL SHORT DIALOGUES (<= 1 sec) ---
    val LAST_OVER_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("LOV_01", listOf(CommentaryDialogue(Commentator.RAHUL, "अंतिम ओवर शुरू!"))),
        DialogueTemplate("LOV_02", listOf(CommentaryDialogue(Commentator.NEHA, "मैच का आखिरी ओवर!"))),
        DialogueTemplate("LOV_03", listOf(CommentaryDialogue(Commentator.RAHUL, "आखिरी छह गेंदें बाकी!"))),
        DialogueTemplate("LOV_04", listOf(CommentaryDialogue(Commentator.NEHA, "सांसें रोक देने वाला ओवर!"))),
        DialogueTemplate("LOV_05", listOf(CommentaryDialogue(Commentator.RAHUL, "फैसले का ओवर!")))
    )

    val LAST_BALL_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("LBL_01", listOf(CommentaryDialogue(Commentator.RAHUL, "मैच की आखिरी गेंद!"))),
        DialogueTemplate("LBL_02", listOf(CommentaryDialogue(Commentator.NEHA, "अंतिम गेंद पर फैसला!"))),
        DialogueTemplate("LBL_03", listOf(CommentaryDialogue(Commentator.RAHUL, "एक गेंद, सब कुछ दांव पर!"))),
        DialogueTemplate("LBL_04", listOf(CommentaryDialogue(Commentator.NEHA, "दिल थाम लीजिए, अंतिम गेंद!"))),
        DialogueTemplate("LBL_05", listOf(CommentaryDialogue(Commentator.RAHUL, "फाइनल डिलीवरी!")))
    )

    val TARGET_CHASE_TEMPLATES: List<DialogueTemplate> = listOf(
        DialogueTemplate("TGT_01", listOf(CommentaryDialogue(Commentator.RAHUL, "लक्ष्य का पीछा शुरू!"))),
        DialogueTemplate("TGT_02", listOf(CommentaryDialogue(Commentator.NEHA, "रन चेज़ का रोमांच!"))),
        DialogueTemplate("TGT_03", listOf(CommentaryDialogue(Commentator.RAHUL, "कड़ा लक्ष्य सामने!"))),
        DialogueTemplate("TGT_04", listOf(CommentaryDialogue(Commentator.NEHA, "लक्ष्य की ओर कदम!"))),
        DialogueTemplate("TGT_05", listOf(CommentaryDialogue(Commentator.RAHUL, "चेज़ में हर रन कीमती!")))
    )
}
