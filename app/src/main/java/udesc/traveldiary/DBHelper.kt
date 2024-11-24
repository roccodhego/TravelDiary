package udesc.traveldiary

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.collections.ArrayList

class DBHelper(context: Context) : SQLiteOpenHelper(context, "banco.db", null, 1) // Versão 1
 {

    val sql = arrayOf(
        "CREATE TABLE viagens (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, data TEXT, nota TEXT, foto TEXT)",
        "INSERT INTO viagens (nome, data, nota) VALUES ('Paraguai', '2000', 'Primeira viagem internacional')",
        "INSERT INTO viagens (nome, data, nota) VALUES ('Chile', '2002', 'Viagem para a neve')"
    )



    override fun onCreate(db: SQLiteDatabase) {
        sql.forEach {
            db.execSQL(it)
        }
    }

     override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
         db.execSQL("DROP TABLE viagens")
         onCreate(db)
     }

     fun viagensSelectById(id: Int): Viagens {
         val db = this.readableDatabase
         val c = db.rawQuery("SELECT * FROM viagens WHERE id = ?", arrayOf(id.toString()))
         var viagem = Viagens()

         if (c.count == 1) {
             c.moveToFirst()

             // Verifica se as colunas existem
             val idIndex = c.getColumnIndex("id")
             val idNome = c.getColumnIndex("nome")
             val idData = c.getColumnIndex("data")
             val idNota = c.getColumnIndex("nota")
             val idFoto = c.getColumnIndex("foto")

             if (idIndex == -1 || idNome == -1 || idData == -1 || idNota == -1) {
                 android.util.Log.e("DBHelper", "Colunas ausentes no cursor para ID: $id")
             } else {
                 val id = c.getInt(idIndex)
                 val nome = c.getString(idNome)
                 val data = c.getString(idData)
                 val nota = c.getString(idNota)
                 val foto = c.getString(idFoto)
                 viagem = Viagens(id, nome, data, nota, foto)
             }
         } else {
             android.util.Log.e("DBHelper", "Nenhuma viagem encontrada com id: $id")
         }
         c.close() // Fecha o cursor
         return viagem
     }



     fun viagensSelectAll(): ArrayList<Viagens>{
        val db = this.readableDatabase //acesso de leitura
        val c = db.rawQuery("SELECT * FROM viagens", null)
        var listaViagens: ArrayList<Viagens> = ArrayList()
        if (c.count > 0){
            c.moveToFirst()
            do{
                val idIndex = c.getColumnIndex("id")
                val idNome = c.getColumnIndex("nome")
                val idData = c.getColumnIndex("data")
                val idNota = c.getColumnIndex("nota")
                val idFoto = c.getColumnIndex("foto")
                val id = c.getInt(idIndex)
                val nome = c.getString(idNome) ?: "Sem Nome"
                val data = c.getString(idData) ?: "Data não informada"
                val nota = c.getString(idNota) ?: "Sem Nota"
                val foto = c.getString(idFoto) ?: "Sem Foto"
                android.util.Log.d("DBHelper", "Viagem recuperada - Foto: $foto")

                listaViagens.add(Viagens(id, nome, data, nota, foto))

            } while (c.moveToNext())
        }
         c.close()
        return listaViagens
    }


     fun viagemInsert(nome: String, data: String, nota: String?, foto: String?): Long {
         val db = this.writableDatabase
         val contentValues = ContentValues()
         contentValues.put("nome", nome)
         contentValues.put("data", data)
         contentValues.put("nota", nota)
         contentValues.put("foto", foto)
         android.util.Log.d("DBHelper", "Inserindo viagem com foto: $foto")
         val resultado = db.insert("viagens", null, contentValues)
         db.close()
         return resultado
     }



     fun viagemUpdate(id: Int, nome: String, data: String, nota: String?, foto: String?): Int {
         val db = this.writableDatabase
         val contentValues = ContentValues()
         contentValues.put("nome", nome)
         contentValues.put("data", data)
         contentValues.put("nota", nota)
         contentValues.put("foto", foto)
         val resultado = db.update("viagens", contentValues, "id = ?", arrayOf(id.toString()))
         db.close()
         return resultado
     }


     fun viagemDelete(id: Int): Int{
        val db = this.writableDatabase
        val resultado = db.delete("viagens", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado
    }

}