<?php

namespace app\controllers;

use app\models\Yahta;
use Yii;
use yii\filters\AccessControl;
use yii\web\Controller;
use yii\web\Response;
use yii\filters\VerbFilter;
use app\models\LoginForm;
use app\models\ContactForm;

class ApiController extends Controller
{

    /**
     * Displays homepage.
     *
     * @return array
     */
    public function actionSearch()
    {
        Yii::$app->response->format = Response::FORMAT_JSON;

        $q = Yahta::search(Yii::$app->request->get());

        return $q->all();
    }

}
