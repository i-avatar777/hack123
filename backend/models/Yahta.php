<?php

namespace app\models;

/**
 * @property int    id
 * @property int    price
 * @property int    type_rent
 * @property int    place_count
 * @property string image
 *
 * Class Yahta
 * @package app\models
 */
class Yahta extends \yii\db\ActiveRecord
{
    public static function search($params)
    {
        $q = self::find();

        if (isset($params['price_from']) and isset($params['price_to'])) {
            if ($params['price_from'] > 0 && $params['price_to'] > 0) {
                $q->andWhere([
                    'and',
                    ['>', 'price', $params['price_from']],
                    ['<', 'price', $params['price_to']],
                ]);
            }
        }
        if (isset($params['type_rent'])) {
            if ($params['type_rent'] > 0) {
                $q->andWhere(['type_rent' => $params['type_rent']]);
            }
        }
        if (isset($params['place_count'])) {
            if ($params['place_count'] > 0) {
                $q->andWhere(['place_count' => $params['place_count']]);
            }
        }

        return $q;
    }
}