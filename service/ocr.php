<?php

require './vendor/autoload.php';

date_default_timezone_set('UTC');

if($_GET['type'] == 'chat') {
    $file_content = file_get_contents('php://input');
    if(empty($file_content)) {
         echo json_encode(['code' => 303, 'msg' => 'file empty']);
         exit();
    }
    $ret = \BadiduBCE\OCR::general($file_content);

    $val = "";
    foreach ($ret['words_result'] as $k => $value) {
        $val .= " " .  preg_replace("/[^\x{4e00}-\x{9fa5}]/iu",'',$value['words']);
    }
    //$val = preg_replace("/[^\x{4e00}-\x{9fa5}]/iu",'',$val);
    echo json_encode(['code' => 200, 'msg' => 'SUCCESS', 'data' => $val]);
} else {
    echo json_encode(['code' => 304,'msg' => 'type error']);
}
exit();
