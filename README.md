## Travel Diary 📝

O aplicativo "Travel Diary" é um gerenciador de diários de viagem que permite aos usuários registrar e organizar informações sobre suas viagens. Com uma estrutura robusta e funcionalidades voltadas para a interação com um banco de dados SQLite, o app oferece aos usuários a capacidade de adicionar, visualizar, atualizar e deletar registros de viagens, bem como associar fotos a cada registro.

### Funcionalidades Principais

1. **Cadastro de Viagens**:
   - Usuários podem adicionar novos registros de viagens, especificando nome, data, notas e fotos.
   - As fotos podem ser capturadas diretamente pela câmera do dispositivo ou selecionadas da galeria.

2. **Visualização de Viagens**:
   - Uma lista de todas as viagens cadastradas é exibida, e os usuários podem selecionar uma viagem para visualizar detalhes.
   - Detalhes incluem nome, data, notas e uma visualização da foto associada.

3. **Edição e Exclusão**:
   - Dentro dos detalhes de cada viagem, usuários têm a opção de editar as informações ou deletar a viagem do banco de dados.
   - A funcionalidade de edição permite atualizar nome, data, notas e substituir ou adicionar uma nova foto.
   - A exclusão remove o registro permanentemente do banco.

4. **Gerenciamento de Fotos**:
   - O aplicativo gerencia as fotos associadas às viagens, permitindo adicionar ou alterar fotos tanto na criação quanto na edição de uma viagem.
   - As fotos são salvas localmente no dispositivo, e os caminhos são armazenados no banco para referência futura.

5. **Interface de Usuário**:
   - O app utiliza um design simples e intuitivo, com botões claros para cada ação e feedback visual apropriado, como mensagens de sucesso ou erro após operações no banco.

### Estrutura Técnica

- **Banco de Dados SQLite**: Utilizado para armazenar os dados das viagens. Inclui operações de inserção, seleção, atualização e exclusão.
- **Android UI Tools**: Utiliza `Activity`, `Intent`, e `AlertDialog` para a interface e navegação entre telas.
- **Tratamento de Imagens**: Manipula imagens através de `Bitmap` e `ImageView` para exibir e alterar fotos.
- **Permissões**: Gerencia permissões de câmera e armazenamento para acessar e salvar fotos.

### Considerações de Design

O design segue padrões modernos de desenvolvimento Android com uso de `Activity` para cada tela principal, binding para interação com componentes da UI, e adaptação dinâmica da interface dependendo das ações do usuário. O aplicativo também é robusto em termos de tratamento de erros, garantindo uma boa experiência mesmo em casos de falha operacional.

Esse aplicativo é ideal para quem gosta de viajar e deseja manter um registro organizado e acessível de suas aventuras, com detalhes e memórias visuais de cada destino.
